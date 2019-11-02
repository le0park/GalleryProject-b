package com.example.galleryproject.ui.timeLine;

import android.graphics.drawable.Drawable;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.LocationUtility;
import com.example.galleryproject.Model.UnitImage;
import com.example.galleryproject.Model.UnitImageGroup;
import com.example.galleryproject.OnCalendarClickListener;
import com.example.galleryproject.OnExpandListener;
import com.example.galleryproject.R;
import com.example.galleryproject.TopCalendarLayout;

import com.example.galleryproject.Model.ImageGroup;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class TimeLineFragment extends Fragment {

    private TimeLineViewModel timeLineViewModel;
    private TimeLineRecyclerView timeLineRecyclerView;
    private TimeLineRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    private ArrayList<ImageGroup> dataset;
    private File file;
    private File[] listFile;
    private ArrayList<String> filePaths;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timeLineViewModel =
                ViewModelProviders.of(this).get(TimeLineViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timeline, container, false);

        timeLineRecyclerView = root.findViewById(R.id.timeLineRecyclerView);
        timeLineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        topCalendar = (TopCalendarLayout) root.findViewById(R.id.topCalendar);
        bottomCalendar = (BottomCalendarLayout) root.findViewById(R.id.bottomCalendar);
        topCalendar.setOnExpandListener(new OnExpandListener() {
            @Override
            public void onExpand() {
                topCalendar.buttonChange();
                bottomCalendar.setVisibility();
            }
        });

        bottomCalendar.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void OnCalendarClick(String year, String month) {
                Toast.makeText(getContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
                topCalendar.buttonChange();
                bottomCalendar.setVisibility();
                topCalendar.setYearTextView(year);
                topCalendar.setMonthTextView(month);
            }
        });

        filePaths = getListOfFile();


        List<Image> images = new ArrayList<>();
        for (String path: filePaths) {
            images.add(new UnitImage(path));
        }

        dataset = new ArrayList<>();
        List<Image> subImages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            subImages.add(images.get(i));
            Log.e("FilePaths : ", images.get(i).getFilePath());
        }
        dataset.add(new UnitImageGroup(subImages, "랄랄라라랄랄라"));

        subImages = new ArrayList<>();
        for (int i = 3; i < 6; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "룰루랄라"));

        subImages = new ArrayList<>();
        for (int i = 6; i < 9; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "중간고사 끝"));

        subImages = new ArrayList<>();
        for (int i = 31; i < 34; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "기말고사 시작"));

        subImages = new ArrayList<>();
        for (int i = 39; i < 42; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "그전엔 최종데모라니"));

        subImages = new ArrayList<>();
        for (int i = 43; i < 46; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "릴리리 맘보"));

        subImages = new ArrayList<>();
        for (int i = 223; i < 226; i++)
            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "쿵따리 샤바라"));

//        subImages = new ArrayList<>();
//        for (int i = 439; i < 442; i++)
//            subImages.add(images.get(i));
//        dataset.add(new UnitImageGroup(subImages, "기억하기 싫은 추억"));
//
//        subImages = new ArrayList<>();
//        for (int i = 500; i < 503; i++)
//            subImages.add(images.get(i));
//        dataset.add(new UnitImageGroup(subImages, "생각나던 사진"));
//
//        subImages = new ArrayList<>();
//        for (int i = 503; i < 506; i++)
//            subImages.add(images.get(i));
        dataset.add(new UnitImageGroup(subImages, "키야~~~~~"));

        adapter = new TimeLineRecyclerViewAdapter(this.getContext(), dataset);
        timeLineRecyclerView.addItemDecoration(getSectionCallback(dataset));

        timeLineRecyclerView.setAdapter(adapter);
//        TextView textView = root.findViewById(R.id.text_timeline);
//        timeLineViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }

    private ArrayList<String> getListOfFile() {
        ArrayList<String> list = new ArrayList<String>();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
        } else {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "DCIM/Camera");
            // Create a new folder if no folder named SDImageTutorial exist
            file.mkdirs();
        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            Log.e("FILE_LIST_LENGTH", String.valueOf(listFile.length));
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].getAbsolutePath().toLowerCase().endsWith(".jpg"))
                    list.add(0, listFile[i].getAbsolutePath());
//                else
//                    Log.e("예외파일", listFile[i].getAbsolutePath());
            }
        }
        return list;
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
                return !dataset.get(position).getDate()
                        .equals(dataset.get(position - 1).getDate());
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
}