package com.example.galleryproject.ui.all;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.ImageCollectionViewModel;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;
import com.example.galleryproject.Model.UnitImage;
import com.example.galleryproject.OnCalendarClickListener;
import com.example.galleryproject.OnExpandListener;
import com.example.galleryproject.PhotoActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.TopCalendarLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private RecyclerView allRecyclerView;
    private AllRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    private File file;
    private File[] listFile;
    private ArrayList<String> filePaths;

    private List<Image> images;
    private ImageCollectionViewModel collectionViewModel;
    private GridLayoutManager gridLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        collectionViewModel = ViewModelProviders.of(getActivity()).get(ImageCollectionViewModel.class);
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);

        View root = inflater.inflate(R.layout.fragment_all, container, false);

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
                topCalendar.buttonChange();
                bottomCalendar.setVisibility();

                //year, month
                int position = adapter.getTimePosition(year, month);
                if (position == -1) { // can't find position
                    String postYear = topCalendar.getYearText();
                    postYear = postYear.substring(0, postYear.length() - 1);
                    bottomCalendar.setBottom_YearTextView(postYear);

                    Toast.makeText(getActivity().getApplicationContext(), "찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                }else { // find position
                    topCalendar.setYearTextView(year);
                    topCalendar.setMonthTextView(month);

                    gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
                    //offset 1 means 1 pixel
                    gridLayoutManager.scrollToPositionWithOffset(position, -1);

                    allRecyclerView.setLayoutManager(gridLayoutManager);

                    Toast.makeText(getActivity().getApplicationContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
                }

            }
        });

        allRecyclerView = root.findViewById(R.id.allRecyclerView);
        allRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

//        filePaths = getListOfFile();
        List<ImageCollection> dataset = collectionViewModel.getImageCollections().getValue();

        images = getImagesFromImageCollections(dataset);
        adapter = new AllRecyclerViewAdapter((MainActivity)getActivity(), images);

        collectionViewModel.getImageCollections().observe(this, new Observer<List<ImageCollection>>() {
            @Override
            public void onChanged(List<ImageCollection> imageCollections) {
                images = getImagesFromImageCollections(imageCollections);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setPhotoClickListener(new PhotoClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PhotoActivity.class);
//                Log.e("FILEPATH", filePaths.get(position));
                String clickPath = images.get(position).getFilePath();
                Image image = new UnitImage(clickPath);
                intent.putExtra("Image", image);
//                intent.putExtra("filePath", filePaths.get(position));
                startActivity(intent);
            }
        });

        allRecyclerView.setAdapter(adapter);
        allRecyclerView.addItemDecoration(new AllRecyclerViewDecoration(10));

        allRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentPosition = ((GridLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//                Log.e("GRID position : ", "" + currentPosition);
                if(!(currentPosition >=0 && currentPosition < images.size())){
                    return;
                }
                LocalDateTime top_date = images.get(currentPosition).getCreationTime();
                String top_year = top_date.format(DateTimeFormatter.ofPattern("yyyy"));
                String top_month = top_date.format(DateTimeFormatter.ofPattern("MM"));

                topCalendar.setYearTextView(top_year);
                topCalendar.setMonthTextView(top_month);
            }
        });

        return root;
    }

    private List<Image> getImagesFromImageCollections(List<ImageCollection> imageCollections){
        images = new ArrayList<>();
        for(ImageCollection ic : imageCollections){
            for(ImageGroup ig : ic.getGroups()){
                for(Image im : ig.getImages())
                    images.add(im);
            }
        }

        return images;
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

}