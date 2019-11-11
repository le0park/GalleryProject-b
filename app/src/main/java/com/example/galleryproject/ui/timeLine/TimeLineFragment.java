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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.LocationUtility;
import com.example.galleryproject.Model.SimGroupAlgorithm;
import com.example.galleryproject.Model.TimeGroupAlgorithm;
import com.example.galleryproject.Model.UnitImage;
import com.example.galleryproject.Model.UnitImageGroup;
import com.example.galleryproject.R;
import com.example.galleryproject.TopCalendarLayout;

import com.example.galleryproject.Model.ImageGroup;
import com.example.galleryproject.ui.survey.SurveyClickListener;
import com.example.galleryproject.ui.survey.SurveyDialogFragment;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class TimeLineFragment extends Fragment {
    public static final String BASE_DIRECTORY_PATH = "DCIM/Camera";
    public static final String EXTENSION_TYPE = ".jpg";

    private LocalDateTime finish = LocalDateTime.now();
    private LocalDateTime start = finish.minusDays(7);
    private int restImageCount = 0;

    private TimeLineViewModel timeLineViewModel;
    private TimeLineRecyclerView timeLineRecyclerView;
    private TimeLineRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    private List<ImageGroup> dataset;
    private List<File> targetFiles;
    private List<Image> selectedImages;

    private List<String> objectPriority = new ArrayList<>();

    private AppDatabase mDb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        SurveyDialogFragment survey = SurveyDialogFragment.getInstance();
        survey.show(getChildFragmentManager(), "Surey Dialog");
        survey.setSurveyClickListener(new SurveyClickListener() {
            @Override
            public void OnSurveyClick(ArrayList<String> list) {
                objectPriority.addAll(list);
//                StringBuilder sb = new StringBuilder();
//                for(String s: objectPriority)
//                    sb.append(s);
//                Log.e("TIMELINE : ", sb.toString());
            }
        });

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

        targetFiles = getListOfFile();
        selectedImages = new ArrayList<>();

        for (File file: targetFiles) {
//            Log.e("ACTIVITY", file.toPath().toString());
            selectedImages.add(new UnitImage(file));
//            Log.e("MainActivity", file.toString() + " UnitImageFile 생성");
        }

        // 시간 오름차순으로 정렬
        selectedImages.sort((i1, i2) -> {
            LocalDateTime d1 = i1.getCreationTime();
            LocalDateTime d2 = i2.getCreationTime();

            return d1.compareTo(d2);
        });

        for (Image image: selectedImages) {
//            Log.e("IMAGES_TIME", image.getCreationTime().toString());
        }

        restImageCount = selectedImages.size();

        // 타겟 이미지 리스트 할당
        List<Image> targetImages = new ArrayList<>(selectedImages);
        targetImages.removeIf((image) -> {
            LocalDateTime t = image.getCreationTime();
            return t.compareTo(start) >= 0 && t.compareTo(finish) < 0;
        });

        new ImageGroupAsyncTask(() -> {})
                .execute(targetImages.toArray(new Image[targetImages.size()]));

        return root;
    }

    private List<File> getListOfFile() {
        File baseDirectory = null;
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
//                Log.e("LENGTH", String.valueOf(files.length));
            }

            return Arrays.asList(files);
        }

        return Arrays.asList();
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<ImageGroup> dataset) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Nullable
            @Override
            public SectionInfo getSectionHeader(int position) {
                ImageGroup imageGroup = dataset.get(position);
                Drawable dot = getContext().getResources().getDrawable(R.drawable.dot);

                String locationMessage = null;
                if (imageGroup.getImages().size() > 0) {
                    Image image = getImageHavingLocation(imageGroup.getImages());

                    if (image == null) {
                        locationMessage = "위치정보 없음";
                    } else {
                        locationMessage = LocationUtility.getLocation(
                            getContext(), image.getLatitude(), image.getLongitude());
                    }
                }

                return new SectionInfo(
                        imageGroup.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
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



    class SimGroupAsyncTask extends AsyncTask<Image, Integer, List<ImageGroup>> {
        @Override
        public List<ImageGroup> doInBackground(Image... images) {
            List<Image> inputImages = Arrays.asList(images);

            SimGroupAlgorithm algorithm = new SimGroupAlgorithm();
            List<ImageGroup> processedGroups = algorithm.processImages(inputImages);
            return processedGroups;
        }

        @Override
        public void onPostExecute(List<ImageGroup> groups) {
            Toast.makeText(getContext(), groups.size() + "개로 유사도 그룹 완료.", Toast.LENGTH_SHORT).show();

            List<Image> resultImages = new ArrayList<>();
            for (ImageGroup group: groups){
//                Log.e("SIMGROUP_THREAD", group.toString() + " | size: " + group.getImages().size());
                resultImages.addAll(group.getImages());
            }

            ImageGroup result = new UnitImageGroup(resultImages);

            // TODO: 후 처리
            timeLineViewModel.insert(result);
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

            publishProgress(processedGroups.size());

            restImageCount -= inputImages.size();
            return processedGroups;
        }

        @Override
        public void onPostExecute(List<ImageGroup> groups) {

            // 유사도 클러스터링
            for (ImageGroup group: groups) {

                // 시간 그룹 내에서 유사도 클러스터링 실행
                Image[] timeImages = new Image[group.getImages().size()];
                new SimGroupAsyncTask()
                        .execute(group.getImages().toArray(timeImages));
            }


            // 남은 사진 시간 클러스터링
            if (restImageCount > 0) {
                // 타겟 이미지 리스트 할당
                finish = start;
                start = finish.minusDays(7);

                // 이미지 타겟팅
                List<Image> targetImages = new ArrayList<>(selectedImages);
                targetImages.removeIf((image) -> {
                    LocalDateTime t = image.getCreationTime();
                    return t.compareTo(start) >= 0 && t.compareTo(finish) < 0;
                });

                // 다음 시간 클러스터링 실행
                new ImageGroupAsyncTask(() -> {})
                        .execute(targetImages.toArray(new Image[targetImages.size()]));
            }
        }
    }

    interface OnTaskFinishedListener {
        void onFinished();
    }
}