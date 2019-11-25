package com.example.galleryproject.ui.timeLine;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.Database.AppExecutors;
import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Database.Entity.DbPriority;
import com.example.galleryproject.Database.Entity.DbRepImage;
import com.example.galleryproject.Model.Adapter.DbImageAdapter;
import com.example.galleryproject.Model.Adapter.DbImageCollectionAdapter;
import com.example.galleryproject.Model.Adapter.DbImageGroupAdapter;
import com.example.galleryproject.DeepLearningModel;
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

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class TimeLineFragment extends Fragment {
    public static final String BASE_DIRECTORY_PATH = "DCIM/Camera";
    public static final String EXTENSION_TYPE = ".jpg";
    private static final int IMAGE_TIME_RANGE = 7;

    private TimeLineViewModel timeLineViewModel;
    private TimeLineRecyclerView timeLineRecyclerView;
    private TimeLineRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    private List<File> targetFiles;
    private List<Image> selectedImages;

    private List<Integer> objectPriority = new ArrayList<>();

    private AppDatabase mDb;
    private ImageGroupLabelAnalyzer analyzer = new ImageGroupLabelAnalyzer();

    private int imageOrderIdx = 0;
    private List<List<Image>> partedImages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        timeLineRecyclerView = root.findViewById(R.id.timeLineRecyclerView);
        timeLineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        timeLineViewModel =
                ViewModelProviders.of(this)
                                  .get(TimeLineViewModel.class);

        timeLineViewModel.getImageGroups()
                         .observe(this, (list) -> {

            // Update the cached copy of the words in the adapter.
            adapter.notifyDataSetChanged();
        });

        List<ImageCollection> dataset = timeLineViewModel.getImageGroups().getValue();
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
            Toast.makeText(getActivity().getApplicationContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
            topCalendar.buttonChange();
            bottomCalendar.setVisibility();
            topCalendar.setYearTextView(year);
            topCalendar.setMonthTextView(month);
        });




        // 데이터베이스 생성
        mDb = AppDatabase.getInstance(getContext());

        AppExecutors.getInstance()
                    .diskIO()
                    .execute(() -> {

                        // 이미지 파일 목록 추출
                        targetFiles = getListOfFile();
                        selectedImages = targetFiles.stream()
                                                    .map(UnitImage::new)
                                                    .collect(Collectors.toList());

                        // 이미 데이터베이스에 저장되어 있는 파일 제거
                        List<DbImage> dbImages = mDb.dbImageDao().getAll();
                        List<Image> newImages = dbImages.stream()
                                                        .map(x -> new DbImageAdapter(x))
                                                        .collect(Collectors.toList());

                        selectedImages.removeIf((image) -> newImages.contains(image));

                        imageOrderIdx = 0;
                        partedImages = splitImagesInDayRange(selectedImages, IMAGE_TIME_RANGE);



                        List<DbPriority> priorities = mDb.dbPriorityDao().getAll();
                        if (priorities.size() != 4) {
                            mDb.dbPriorityDao().deleteAll();
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                SurveyDialogFragment survey = SurveyDialogFragment.getInstance();
                                survey.show(getChildFragmentManager(), "Survey Dialog");
                                survey.setSurveyClickListener((ArrayList<String> categories) -> {

                                    for (int i = 0; i < categories.size(); i++) {

                                        String category = categories.get(i);
                                        int categoryInt = Category.getValue(category);
                                        objectPriority.add(categoryInt);

                                        DbPriority priority = new DbPriority();
                                        priority.setCategory(categoryInt);
                                        priority.setRank(i);
                                        AppExecutors.getInstance().diskIO().execute(() -> {
                                            mDb.dbPriorityDao().insert(priority);
                                        });
                                    }

                                    objectPriority.add(Category.ETC);

                                    int[] priority = objectPriority.stream()
                                            .mapToInt(Integer::intValue)
                                            .toArray();

                                    ImageGroupLabelAnalyzer.setLabelPriority(priority);

                                    if (partedImages.size() > 0) {
                                        Image[] images = partedImages.get(imageOrderIdx).stream()
                                                .toArray(Image[]::new);

                                        new ImageGroupAsyncTask(() -> {}).execute(images);
                                    }
                                });
                            });
                        } else {
                            objectPriority = new ArrayList<>();
                            objectPriority.add(0);
                            objectPriority.add(0);
                            objectPriority.add(0);
                            objectPriority.add(0);
                            objectPriority.add(0);

                            for (DbPriority priority: priorities) {
                                objectPriority.set(priority.rank, priority.category);
                            }

                            objectPriority.set(4, Category.ETC);
                            int[] priority = objectPriority.stream()
                                    .mapToInt(Integer::intValue)
                                    .toArray();
                            ImageGroupLabelAnalyzer.setLabelPriority(priority);

                            if (partedImages.size() > 0) {
                                Image[] images = partedImages.get(imageOrderIdx).stream()
                                        .toArray(Image[]::new);

                                new ImageGroupAsyncTask(() -> {}).execute(images);
                            }
                        }
                    });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        timeLineViewModel.getImageGroups().clear(true);
        AppExecutors.getInstance()
                    .diskIO()
                    .execute(() -> {
                        int offset = 0;
                        int size = -1;
                        while ((offset == 0 && size == -1) ||
                                (size != 0)){
                            List<ImageCollection> collections = getCollectionsFromDbByRange(10, offset);

                            size = collections.size();
                            offset += size;
                            if (size > 0) {
                                AppExecutors.getInstance()
                                        .mainThread()
                                        .execute(() -> timeLineViewModel.insertAll(collections));
                            }
                        }
                    });

    }


    @WorkerThread
    public List<ImageCollection> getCollectionsFromDb() {
        List<ImageCollection> collections = new ArrayList<>();
        List<DbImageCollection> dbCollections =
                mDb.dbImageCollectionDao().getAll();

        for (DbImageCollection dbCollection: dbCollections) {
            List<DbImageGroup> dbImageGroups =
                    mDb.dbImageGroupDao().loadAllWithCollectionId(dbCollection.id);

            List<ImageGroup> imageGroups = new ArrayList<>();
            for (DbImageGroup group: dbImageGroups) {
                List<DbImage> dbImages = mDb.dbImageDao()
                        .loadWithGroupId(group.id);

                List<Image> newImages = dbImages.stream()
                        .map(DbImageAdapter::new)
                        .collect(Collectors.toList());

                imageGroups.add(new DbImageGroupAdapter(group, newImages));
            }

            List<DbImage> dbImages = mDb.dbRepImageDao().getRepImageForCollection(dbCollection.id);
            List<Image> repImages = dbImages.stream()
                    .map(DbImageAdapter::new)
                    .collect(Collectors.toList());

            collections.add(new DbImageCollectionAdapter(dbCollection, imageGroups, repImages));
        }

        return collections;
    }

    @WorkerThread
    public List<ImageCollection> getCollectionsFromDbByRange(int count, int offset) {
        List<ImageCollection> collections = new ArrayList<>();
        List<DbImageCollection> dbCollections =
                mDb.dbImageCollectionDao().getRange(count, offset);

        for (DbImageCollection dbCollection: dbCollections) {
            List<DbImageGroup> dbImageGroups =
                    mDb.dbImageGroupDao().loadAllWithCollectionId(dbCollection.id);

            List<ImageGroup> imageGroups = new ArrayList<>();
            for (DbImageGroup group: dbImageGroups) {
                List<DbImage> dbImages = mDb.dbImageDao()
                        .loadWithGroupId(group.id);

                List<Image> newImages = dbImages.stream()
                        .map(DbImageAdapter::new)
                        .collect(Collectors.toList());

                imageGroups.add(new DbImageGroupAdapter(group, newImages));
            }

            List<DbImage> dbImages = mDb.dbRepImageDao().getRepImageForCollection(dbCollection.id);
            List<Image> repImages = dbImages.stream()
                    .map(DbImageAdapter::new)
                    .collect(Collectors.toList());

            collections.add(new DbImageCollectionAdapter(dbCollection, imageGroups, repImages));
        }

        return collections;
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<ImageCollection> data) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                ImageCollection imageCollection = data.get(position);
//                Drawable dot = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.dot, null);

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

                //TODO dot drwable 수정
                return new SectionInfo(
                        imageCollection.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        locationMessage,
                        null);
            }

            @Override
            public boolean isSection(int position) {
                LocalDate dt1 = data.get(position).getDate().toLocalDate();
                LocalDate dt2 = data.get(position - 1).getDate().toLocalDate();

                return !dt1.equals(dt2);
            }

            private Image getImageHavingLocation(List<Image> images) {
                if (images.size() > 0) {
                    for (Image image : images) {
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


    /**
     * 정렬된 image를 일 간격에 맞게 분리함.
     * image는 오름차순.
     * @param images
     * @param days
     * @return
     */
    private List<List<Image>> splitImagesInDayRange(List<Image> images, int days) {
        sortImages(images, 0);

        List<List<Image>> partitioned = new ArrayList<>();

        LocalDateTime line = null;

        int startIdx = 0, finishIdx = 0, i = 0;
        while (i <= images.size()) {
            if (line == null && i == images.size()) {
                i += 1;
                continue;
            }

            if (i == images.size()) {
                List<Image> part = images.subList(startIdx, i);
                partitioned.add(part);
                i += 1;
                continue;
            }

            Image im = images.get(i);
            if (line == null) {
                line = im.getCreationTime();
                i += 1;
                continue;
            }

            LocalDateTime start = line.minusDays(days);
            LocalDateTime imTime = im.getCreationTime();
            if(imTime.isBefore(start)) {
                finishIdx = i;

                List<Image> part = images.subList(startIdx, finishIdx);
                partitioned.add(part);

                startIdx = finishIdx;
                line = null;
                continue;
            }

            i++;
        }

        return partitioned;
    }

    /**
     * 시간 순 정렬
     * @param images
     * @param order (1: 오름차순, 0: 내림차순)
     */
    private static void sortImages(List<Image> images, int order) {
        // 시간 오름차순으로 정렬
        images.sort((i1, i2) -> {
            LocalDateTime d1 = i1.getCreationTime();
            LocalDateTime d2 = i2.getCreationTime();

            if (order > 0) {
                return d1.compareTo(d2);
            } else {
                return d2.compareTo(d1);
            }
        });
    }

    private List<File> getListOfFile() {
        File baseDirectory = null;

        // 파일 목록 추출
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity().getApplicationContext(), "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
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
//            Toast.makeText(getActivity().getApplicationContext(), groups.size() + "개로 유사도 그룹 완료.", Toast.LENGTH_SHORT).show();

            ImageCollection result = new ImageCollection(groups);

            ImageGroup[] inputGroups = new ImageGroup[groups.size()];
            groups.toArray(inputGroups);

            // MLkit labeling thread
            new AsyncLabelingTask((labelGroups) -> {
                // callback
                analyzer.setGroups(groups);
                analyzer.setLabelGroups(labelGroups);
                analyzer.analyze();

                /**
                 * TODO: 대표이미지 뽑아서 result.setRepImages(...) 로 대표이미지 할당해야함.
                 */
                Log.e("LABEL_ANALYZER", "size: " + groups.size());
                Log.e("LABEL_ANALYZER", analyzer.toString());

                DeepLearningModel model = new DeepLearningModel(getActivity());
                Interpreter tf_lite = model.getTfliteInterpreter("kanghee_model.tflite");

                Log.e("getXSize : ", analyzer.getX().size() + "");
                float[][][] input = model.parseModelInput(analyzer.getX());
                float[][] priority = new float[input.length][1];

                tf_lite.run(input, priority);

                for(int i=0;i<input.length;i++)
                    Log.e("PRIORITY : ", "size : " + input.length + " == " + priority.length + "  /  " + priority[i][0] + "");

                List<Image> images = result.getGroups().get(0).getImages();
                List<Image> repImages = model.getRepImages(analyzer.getRepImageCandidate(), priority);

                result.setRepImages(repImages);
                
                timeLineViewModel.insert(result);
                listener.onFinished();

                // 생성된 타임라인 저장 thread
                AppExecutors.getInstance().diskIO().execute(() -> {
                    DbImageCollection newDbImageCollection = new ImageCollectionAdapter(result);

                    long collectionId = mDb.dbImageCollectionDao().insert(newDbImageCollection);

                    // DbImageCollection <- DbImageGroup
                    List<DbImageGroup> newDbImageGroups =
                            result.getGroups().stream()
                                              .map(ImageGroupAdapter::new)
                                              .collect(Collectors.toList());

                    List<Long> dbImageGroupIds =
                            mDb.dbImageCollectionDao()
                               .insertWithGroups((int) collectionId, newDbImageGroups);

                    List<List<Image>> newGroupOfImages = result.getGroups().stream()
                                                               .map(ImageGroup::getImages)
                                                               .collect(Collectors.toList());

                    List<Long> imageIdsForRepImage = new ArrayList<>();

                    // DbGroup <- DbImage
                    List<Long> dbImageIds;
                    for (int i = 0; i < dbImageGroupIds.size(); i++) {
                        Long groupId = dbImageGroupIds.get(i);

                        List<Image> iImageGroup = newGroupOfImages.get(i);
                        List<DbImage> newDbImages = iImageGroup.stream()
                                                               .map(ImageAdapter::new)
                                                               .collect(Collectors.toList());

                        for(DbImage image: newDbImages) {
                            long dbImageId = mDb.imagesWithImageGroupDao()
                                                .insertImageWithGroupId(groupId, image);

                            // 대표사진 imageId 값 저장
                            for (Image rim: repImages) {
                                if (rim.equals(image.path)) {
                                    imageIdsForRepImage.add(dbImageId);
                                }
                            }
                        }

                        dbImageIds = mDb.imagesWithImageGroupDao()
                                        .insertImagesWithImageGroupId(groupId, newDbImages);


                        List<LabelGroup> labelGroup = labelGroups.get(i);

                        // DbImage <- DbLabel
                        for (int j = 0; dbImageIds != null && j < dbImageIds.size(); j++) {
                            long dbImageId = dbImageIds.get(j);

                            List<Label> labels = labelGroup.get(j).getLabels();
                            List<DbLabel> dbLabels = labels.stream()
                                    .map(LabelAdapter::new)
                                    .collect(Collectors.toList());

                            mDb.imagesWithImageGroupDao()
                                    .insertLabelsWithImageId(dbImageId, dbLabels);

                        }
                    }

                    for (long imageId: imageIdsForRepImage) {
                        DbRepImage repImage = new DbRepImage();

                        mDb.dbRepImageDao()
                           .insertWithCollectionAndImage(repImage, (int) collectionId, (int) imageId);
                    }
                });
            }).execute(inputGroups);
        }
    }


    /**
     * 이미지 타임라인 그룹 생성 쓰레드
     */
    private class ImageGroupAsyncTask extends AsyncTask<Image, Integer, List<ImageGroup>> {
        OnTaskFinishedListener listener;

        private CountDownLatch latch;
        ImageGroupAsyncTask(OnTaskFinishedListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public List<ImageGroup> doInBackground(Image... images) {

            List<Image> inputImages = Arrays.asList(images);

            // 일정 시간 단위로 나눔
            TimeGroupAlgorithm algorithm = new TimeGroupAlgorithm();
            List<ImageGroup> processedGroups = algorithm.processImages(inputImages);

            publishProgress(processedGroups.size());

            return processedGroups;
        }

        @Override
        public void onPostExecute(List<ImageGroup> groups) {
            // 유사도 클러스터링
            int groupsCount = groups.size();
            latch = new CountDownLatch(groupsCount);


            for (ImageGroup group : groups) {
                // 시간 그룹 내에서 유사도 클러스터링 실행
                Image[] timeImages = new Image[group.getImages().size()];

                new SimGroupAsyncTask(() -> startNextImages(latch)).execute(group.getImages().toArray(timeImages));
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
                imageOrderIdx += 1;

                if (imageOrderIdx >= partedImages.size()) {
                    return;
                }

                // 다음 시간 클러스터링 실행
                int nextImagesCount = partedImages.get(imageOrderIdx).size();

                Image[] nextImages = new Image[nextImagesCount];
                partedImages.get(imageOrderIdx).toArray(nextImages);

                new ImageGroupAsyncTask(() -> {}).execute(nextImages);
                }
            }
        }
    }

    /**
     * Thread for Async MLKit DbImage Labeler
     * labeling 하고자 하는 이미지 -> label list
     */
    class AsyncLabelingTask extends AsyncTask<ImageGroup, Void, List<List<LabelGroup>>> {
        List<String> filenames = new ArrayList<>();

        private OnLabelTaskFinishedListener listener;

        AsyncLabelingTask(OnLabelTaskFinishedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<List<LabelGroup>> doInBackground(ImageGroup... groups) {
            List<List<LabelGroup>> allLabelGroups = new ArrayList<>();
            for (ImageGroup group: groups) {
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

        LabelGroup resultLabelGroup;
        private LabelGroup processImagesWithMlkit(Image imageFile) {
            final CountDownLatch latch = new CountDownLatch(1);
            File file = imageFile.getFile();

            ImageFileLabeler imageFileLabeler = new ImageFileLabeler(file, new ImageFileLabeler.ImageFileLabelerListener() {
                @Override
                public void onSuccess(File file, List<Label> labels) {
                    resultLabelGroup = new LabelGroup(labels);
                    filenames.add(file.getName());

                    latch.countDown();
                }

                @Override
                public void onFailure(File file) {
                    latch.countDown();
                    Log.e("ImageFileLabeler", file.getName());
                }
            });

            imageFileLabeler.process();

            try {
                // labeler가 종료되면 return 할 수 있도록
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("MLKIT_ASYNC_TASK", e.getMessage());
            }

            return resultLabelGroup;
        }

    }

    interface OnTaskFinishedListener {
        void onFinished();
    }

    interface OnLabelTaskFinishedListener {
        void onFinished(List<List<LabelGroup>> groups);
    }

