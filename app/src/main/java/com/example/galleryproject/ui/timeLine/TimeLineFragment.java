package com.example.galleryproject.ui.timeLine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.Database.AppExecutors;
import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Database.Entity.DbInputData;
import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Model.Adapter.DbImageAdapter;
import com.example.galleryproject.Model.Adapter.DbImageCollectionAdapter;
import com.example.galleryproject.Model.Adapter.DbImageGroupAdapter;
import com.example.galleryproject.Model.Adapter.ImageAdapter;
import com.example.galleryproject.Model.Adapter.ImageCollectionAdapter;
import com.example.galleryproject.Model.Adapter.ImageGroupAdapter;
import com.example.galleryproject.Model.Adapter.LabelAdapter;
import com.example.galleryproject.Model.Category;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageGroup;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroupLabelAnalyzer;
import com.example.galleryproject.Model.Label;
import com.example.galleryproject.Model.LabelGroup;
import com.example.galleryproject.Model.LocationUtility;
import com.example.galleryproject.Model.SimGroupAlgorithm;
import com.example.galleryproject.Model.TimeGroupAlgorithm;
import com.example.galleryproject.Model.UnitImage;
import com.example.galleryproject.R;
import com.example.galleryproject.TopCalendarLayout;

import com.example.galleryproject.Util.ImageFileLabeler;
import com.example.galleryproject.ui.survey.SurveyDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import kotlin.Suppress;
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class TimeLineFragment extends Fragment {
    public static final String BASE_DIRECTORY_PATH = "DCIM/Camera";
    public static final String EXTENSION_TYPE = ".jpg";
    private static final int IMAGE_TIME_RANGE = 7;

    private LocalDateTime finish = LocalDateTime.now();
    private LocalDateTime start = finish.minusDays(IMAGE_TIME_RANGE);
    private int restImageCount = 0;

    private TimeLineViewModel timeLineViewModel;
    private TimeLineRecyclerView timeLineRecyclerView;
    private TimeLineRecyclerViewAdapter adapter;
    private Button shareButton;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    private List<ImageCollection> dataset;
    private List<File> targetFiles;
    private List<Image> selectedImages;

    private List<Integer> objectPriority = new ArrayList<>();

    private AppDatabase mDb;
    private ImageGroupLabelAnalyzer analyzer = new ImageGroupLabelAnalyzer();
    private CountDownLatch latch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        timeLineRecyclerView = root.findViewById(R.id.timeLineRecyclerView);
        timeLineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        timeLineViewModel =
                ViewModelProviders.of(this).get(TimeLineViewModel.class);
        timeLineViewModel.getImageGroups().observe(this, (list) -> {
            // Update the cached copy of the words in the adapter.
            adapter.notifyDataSetChanged();
        });

        dataset = timeLineViewModel.getImageGroups().getValue();
        adapter = new TimeLineRecyclerViewAdapter(this.getContext(), dataset);

        timeLineRecyclerView.addItemDecoration(getSectionCallback((ArrayList) dataset));
        timeLineRecyclerView.setAdapter(adapter);


        topCalendar = root.findViewById(R.id.topCalendar);
        bottomCalendar = root.findViewById(R.id.bottomCalendar);
        topCalendar.setOnExpandListener(() -> {
            topCalendar.buttonChange();
            bottomCalendar.setVisibility();
        });

        bottomCalendar.setOnCalendarClickListener((year, month) -> {
            Toast.makeText(getContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
            topCalendar.buttonChange();
            bottomCalendar.setVisibility();
            topCalendar.setYearTextView(year);
            topCalendar.setMonthTextView(month);
        });

        shareButton = root.findViewById(R.id.shareButton);
        shareButton.setOnClickListener((view) -> {
            AppExecutors.getInstance().diskIO().execute(() -> {
                List<DbInputData> inputDataList = mDb.dbInputDataDao().getAll();

                JSONArray ja = new JSONArray();
                for (DbInputData inputData: inputDataList) {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("in", inputData.getJson());
                        jo.put("test", inputData.isSelected());

                        ja.put(jo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON", e.getMessage());
                    }

                }

                try {
                    final String message = ja.toString(2);

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON", e.getMessage());
                }

            });
        });




        // 데이터베이스 생성
        mDb = AppDatabase.getInstance(getContext());

        AppExecutors.getInstance().diskIO().execute(() -> {
            List<ImageCollection> collections = new ArrayList<>();

            List<DbImageCollection> dbCollections = mDb.dbImageCollectionDao().getAll();
            for (DbImageCollection dbCollection: dbCollections) {
                List<DbImageGroup> dbImageGroups =
                        mDb.dbImageGroupDao().loadAllWithCollectionId(dbCollection.id);

                List<ImageGroup> imageGroups = new ArrayList<>();
                for (DbImageGroup group: dbImageGroups) {
                    List<DbImage> dbImages = mDb.dbImageDao().loadWithGroupId(group.id);
                    List<Image> newImages = dbImages
                            .stream()
                            .map(x -> new DbImageAdapter(x))
                            .collect(Collectors.toList());

                    imageGroups.add(new DbImageGroupAdapter(group, newImages));
                }

                collections.add(new DbImageCollectionAdapter(dbCollection, imageGroups));
            }

            mDb.dbInputDataDao().reset();

            AppExecutors.getInstance().mainThread().execute(()->{
                timeLineViewModel.insertAll(collections);
            });


            // 이미지 파일 목록 추출
            targetFiles = getListOfFile();
            selectedImages = new ArrayList<>();

            for (File file: targetFiles) {
                selectedImages.add(new UnitImage(file));
            }


            // 이미 데이터베이스에 저장되어 있는 파일 제거
            List<DbImage> dbImages = mDb.dbImageDao().getAll();
            List<Image> newImages = dbImages.stream()
                                            .map(x -> new DbImageAdapter(x))
                                            .collect(Collectors.toList());

            selectedImages.removeIf((image) -> newImages.contains(image));

            // 시간 오름차순으로 정렬
            selectedImages.sort((i1, i2) -> {
                LocalDateTime d1 = i1.getCreationTime();
                LocalDateTime d2 = i2.getCreationTime();

                return d1.compareTo(d2);
            });

            // 사진 갯수
            restImageCount = selectedImages.size();

            AppExecutors.getInstance().mainThread().execute(()->{
                SurveyDialogFragment survey = SurveyDialogFragment.getInstance();
                survey.show(getChildFragmentManager(), "Survey Dialog");
                survey.setSurveyClickListener((ArrayList<String> categories) -> {
                    for (String category: categories) {
                        objectPriority.add(
                                Category.getValue(category));
                    }

                    objectPriority.add(Category.ETC);

                    int[] priority = objectPriority.stream()
                            .mapToInt(Integer::intValue)
                            .toArray();

                    ImageGroupLabelAnalyzer.setLabelPriority(priority);

                    // 타겟 이미지 리스트 할당
                    List<Image> targetImages = new ArrayList<>(selectedImages);
                    targetImages.removeIf((image) -> {
                        LocalDateTime t = image.getCreationTime();
                        return t.compareTo(start) < 0 || t.compareTo(finish) > 0;
                    });

                    new ImageGroupAsyncTask(() -> {})
                            .execute(targetImages.toArray(new Image[targetImages.size()]));
                });
            });
        });

        return root;
    }



    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<ImageCollection> dataset) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                ImageCollection imageCollection = dataset.get(position);
                Drawable dot = getContext().getResources().getDrawable(R.drawable.dot);

                String locationMessage = null;
                if (imageCollection.getGroups().size() > 0) {
                    ImageGroup group = imageCollection.getGroups().get(0);
                    Image image = getImageHavingLocation(group.getImages());

                    if (image == null) {
                        locationMessage = "위치정보 없음";
                    } else {
                        locationMessage = LocationUtility.getLocation(
                            getContext(), image.getLatitude(), image.getLongitude());
                    }
                }

                return new SectionInfo(
                        imageCollection.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        locationMessage,
                        dot);
            }

            @Override
            public boolean isSection(int position) {
                LocalDate dt1 = dataset.get(position).getDate().toLocalDate();
                LocalDate dt2 = dataset.get(position - 1).getDate().toLocalDate();

                return !dt1.equals(dt2);
            }

            private Image getImageHavingLocation(List<Image> images) {
                if (images.size() > 0) {
                    for (Image image: images) {
                        if (image.getLatitude() != 0.0 &&
                            image.getLongitude() != 0.0) {
                            return image;
                        }
                    }
                }

                return null;
            }
        };
    }



    private List<File> getListOfFile() {
        File baseDirectory = null;

        // 파일 목록 추출
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
        } else {

            // Locate the image folder in your SD Card
            baseDirectory = new File(Environment.getExternalStorageDirectory()
                    + File.separator + BASE_DIRECTORY_PATH);

            // Create a new folder if no folder named SDImageTutorial exist
            baseDirectory.mkdirs();
        }

        if (baseDirectory != null && baseDirectory.isDirectory()) {
            File[] files = baseDirectory.listFiles(
                    (File dir, String name) -> name.toLowerCase().endsWith(EXTENSION_TYPE));

            if (files != null) {
                Log.e("LENGTH", String.valueOf(files.length));
            }

            return Arrays.asList(files);
        }

        return Arrays.asList();
    }


    class SimGroupAsyncTask extends AsyncTask<Image, Integer, List<ImageGroup>> {
        OnTaskFinishedListener listener;

        public SimGroupAsyncTask(OnTaskFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        public List<ImageGroup> doInBackground(Image... images) {
            List<Image> inputImages = Arrays.asList(images);

            SimGroupAlgorithm algorithm = new SimGroupAlgorithm();
            List<ImageGroup> processedGroups = algorithm.processImages(inputImages);
            return processedGroups;
        }

        @Override
        public void onPostExecute(List<ImageGroup> groups) {
//            Toast.makeText(getContext(), groups.size() + "개로 유사도 그룹 완료.", Toast.LENGTH_SHORT).show();

//            List<Image> resultImages = new ArrayList<>();
//            for (ImageGroup group: groups){
////                Log.e("SIMGROUP_THREAD", group.toString() + " | size: " + group.getImages().size());
//                resultImages.addAll(group.getImages());
//            }

            ImageCollection result = new ImageCollection(groups);
//            ImageGroup result = new UnitImageGroup(resultImages);


            new AsyncLabelingTask((labelGroups) -> {
                analyzer.setGroups(groups);
                analyzer.setLabelGroups(labelGroups);
                analyzer.analyze();

                listener.onFinished();

                result.setMemo(analyzer.toString());
                timeLineViewModel.insert(result);

                AppExecutors.getInstance().diskIO().execute(() -> {
                    // doSomething();
                    DbImageCollection newDbImageCollection = new ImageCollectionAdapter(result);

                    List<DbImageGroup> newDbImageGroups = result.getGroups()
                                                                .stream()
                                                                .map(x -> new ImageGroupAdapter(x))
                                                                .collect(Collectors.toList());

                    List<Long> dbImageGroupIds =
                            mDb.dbImageCollectionDao().insertWithGroups(newDbImageCollection, newDbImageGroups);

                    List<List<Image>> newGroupOfImages = result.getGroups()
                                                              .stream()
                                                              .map(ImageGroup::getImages)
                                                              .collect(Collectors.toList());


                    List<Long> dbImageIds = null;
                    for (int i = 0; i < dbImageGroupIds.size(); i++) {
                        Long groupId = dbImageGroupIds.get(i);
                        List<DbImage> newDbImages =
                                newGroupOfImages.get(i)
                                                .stream()
                                                .map(x -> new ImageAdapter(x))
                                                .collect(Collectors.toList());


                        dbImageIds = mDb.imagesWithImageGroupDao()
                                .insertImagesWithImageGroupId(groupId, newDbImages);

                        List<LabelGroup> labelGroup = labelGroups.get(i);


                        for (int j = 0; dbImageIds != null && j < dbImageIds.size(); j++) {
                            long dbImageId = dbImageIds.get(j);
                            List<Label> labels = labelGroup.get(j).getLabels();
                            List<DbLabel> dbLabels = labels.stream()
                                    .map(x -> new LabelAdapter(x))
                                    .collect(Collectors.toList());

                            mDb.imagesWithImageGroupDao().insertLabelsWithImageId(dbImageId, dbLabels);
                        }
                    }

                });
            }).execute(groups);
        }
    }


    class ImageGroupAsyncTask extends AsyncTask<Image, Integer, List<ImageGroup>> {
        public OnTaskFinishedListener listener;

        public ImageGroupAsyncTask(OnTaskFinishedListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public List<ImageGroup> doInBackground(Image... images) {

            List<Image> inputImages = Arrays.asList(images);

            TimeGroupAlgorithm algorithm = new TimeGroupAlgorithm();
            List<ImageGroup> processedGroups = algorithm.processImages(inputImages);

            return processedGroups;
        }

        @Override
        public void onPostExecute(List<ImageGroup> groups) {
//            Log.e("IMAGEGROUP_ASYNCTASK", "Group size: " + groups.size() + "");

            // 유사도 클러스터링
            int groupsCount = groups.size();
            latch = new CountDownLatch(groupsCount);

            // 현재 남은 이미지 갯수 측정
            int allImageCount = 0;
            for (ImageGroup group: groups) {
                allImageCount = group.getImages().size();
            }
            restImageCount -= allImageCount;

            for (ImageGroup group: groups) {
                // 시간 그룹 내에서 유사도 클러스터링 실행
                new SimGroupAsyncTask(() -> {
                    startNextImages(latch);
                }).execute(group.getImages().stream().toArray(Image[]::new));
            }

            if (groups.size() <= 0) {
                startNextImages(latch);
            }
        }

        private void startNextImages(CountDownLatch latch) {

            if (latch.getCount() > 0) {
                latch.countDown();
            }

            if (latch.getCount() == 0) {
                // 남은 사진 시간 클러스터링
                if (restImageCount > 0) {

                    // 타겟 이미지 리스트 할당
                    finish = start;
                    start = finish.minusDays(IMAGE_TIME_RANGE);

                    // 이미지 타겟팅
                    List<Image> targetImages = new ArrayList<>(selectedImages);
                    targetImages.removeIf((image) -> {
                        LocalDateTime t = image.getCreationTime();
                        return t.compareTo(start) < 0 || t.compareTo(finish) > 0;
                    });

                    // 다음 시간 클러스터링 실행
                    new ImageGroupAsyncTask(() -> {})
                            .execute(targetImages.toArray(new Image[targetImages.size()]));
                }
            }
        }
    }



    /**
     * Thread for Async MLKit DbImage Labeler
     * labeling 하고자 하는 이미지 -> label list
     */
    private class AsyncLabelingTask extends AsyncTask<List<ImageGroup>, Void, List<List<LabelGroup>>> {
        LabelGroup tmpLabelGroup;
        List<String> filenames = new ArrayList<>();

        private OnLabelTaskFinishedListener listener;

        AsyncLabelingTask(OnLabelTaskFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<List<LabelGroup>> doInBackground(List<ImageGroup>... groups) {
            List<List<LabelGroup>> allLabelGroups = new ArrayList<>();
            for (ImageGroup group: groups[0]) {
                List<Image> images = group.getImages();
                List<LabelGroup> labelGroups = images.stream()
                                                    .map(x -> processImagesWithMlkit(x))
                                                    .collect(Collectors.toList());

                allLabelGroups.add(labelGroups);
            }

            return allLabelGroups;
        }

        @Override
        protected void onPostExecute(List<List<LabelGroup>> labels) {
            super.onPostExecute(labels);

            listener.onFinished(labels);
        }

        private LabelGroup processImagesWithMlkit(Image imageFile) {
            final CountDownLatch latch = new CountDownLatch(1);
            File file = imageFile.getFile();

            ImageFileLabeler imageFileLabeler = new ImageFileLabeler(file, new ImageFileLabeler.ImageFileLabelerListener() {
                @Override
                public void onSuccess(File file, List<Label> labels) {
                    tmpLabelGroup = new LabelGroup(labels);
                    filenames.add(file.getName());

                    latch.countDown();
                }

                @Override
                public void onFailure(File file) {
                    Log.e("ImageFileLabeler", file.getName());
                    latch.countDown();
                }
            });

            imageFileLabeler.process();

            try {
                latch.await();
            } catch (InterruptedException e) {
                Log.e("MLKIT_ASYNC_TASK", e.toString());
            }

            return tmpLabelGroup;
        }

    }

    interface OnTaskFinishedListener {
        void onFinished();
    }

    interface OnLabelTaskFinishedListener {
        void onFinished(List<List<LabelGroup>> groups);
    }

}