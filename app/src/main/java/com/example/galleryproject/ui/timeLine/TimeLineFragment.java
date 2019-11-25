package com.example.galleryproject.ui.timeLine;

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
import com.example.galleryproject.ImageCollectionViewModel;
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

    private ImageCollectionViewModel collectionViewModel;
    private TimeLineRecyclerView timeLineRecyclerView;
    private TimeLineRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;
    private AppDatabase mDb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        timeLineRecyclerView = root.findViewById(R.id.timeLineRecyclerView);
        timeLineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionViewModel = ViewModelProviders.of(getActivity()).get(ImageCollectionViewModel.class);

        List<ImageCollection> dataset = collectionViewModel.getImageCollections().getValue();
        adapter = new TimeLineRecyclerViewAdapter(this.getContext(), dataset);

        collectionViewModel.getImageCollections()
                .observe(this, (list) -> {
                    // Update the cached copy of the words in the adapter.
                    adapter.notifyDataSetChanged();
                });

        timeLineRecyclerView.addItemDecoration(getSectionCallback((ArrayList) dataset));
        timeLineRecyclerView.setAdapter(adapter);


        topCalendar = root.findViewById(R.id.topCalendar);
        bottomCalendar = root.findViewById(R.id.bottomCalendar);
        topCalendar.setOnExpandListener(() -> {
            topCalendar.buttonChange();
            bottomCalendar.setVisibility();
        });

        bottomCalendar.setOnCalendarClickListener((year, month) -> {
            topCalendar.buttonChange();
            bottomCalendar.setVisibility();

            //year, month
            int position = adapter.getTimePosition(year, month);
            if (position == -1) { // can't find position
                String postYear = topCalendar.getYearText();
                postYear = postYear.substring(0, postYear.length() - 1);
                bottomCalendar.setBottom_YearTextView(postYear);

                Toast.makeText(getActivity().getApplicationContext(), "없다임마", Toast.LENGTH_LONG).show();
            } else { // find position
                topCalendar.setYearTextView(year);
                topCalendar.setMonthTextView(month);

                LinearLayoutManager lm = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                //offset 1 means 1 pixel
                lm.scrollToPositionWithOffset(position, 1);
                timeLineRecyclerView.setLayoutManager(lm);

                Toast.makeText(getActivity().getApplicationContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
            }
        });

        // 데이터베이스 생성
        mDb = AppDatabase.getInstance(getContext());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
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
}


