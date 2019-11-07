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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.BottomCalendarLayout;
import com.example.galleryproject.MainActivity;
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
import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private AllViewModel allViewModel;
    private RecyclerView allRecyclerView;
    private AllRecyclerViewAdapter adapter;

    private TopCalendarLayout topCalendar;
    private BottomCalendarLayout bottomCalendar;

    File file;
    File[] listFile;
    private ArrayList<String> filePaths;
    FirebaseVisionImage firebaseImage;
    FirebaseVisionImageLabeler labeler;
    //    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allViewModel = ViewModelProviders.of(this).get(AllViewModel.class);
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
                Toast.makeText(getContext(), "Click : " + year + "년 " + month + "월", Toast.LENGTH_LONG).show();
                topCalendar.buttonChange();
                bottomCalendar.setVisibility();
                topCalendar.setYearTextView(year);
                topCalendar.setMonthTextView(month);
            }
        });

        labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        allRecyclerView = root.findViewById(R.id.allRecyclerView);
        allRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        filePaths = getListOfFile();

        adapter = new AllRecyclerViewAdapter((MainActivity) getActivity(), filePaths);
        adapter.setPhotoClickListener(new PhotoClickListener() {
            @Override
            public void onItemClick(int position) {
//                showObjects(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), PhotoActivity.class);
//                Log.e("FILEPATH", filePaths.get(position));
                intent.putExtra("filePath", filePaths.get(position));
                startActivity(intent);
            }
        });

        allRecyclerView.setAdapter(adapter);
        allRecyclerView.addItemDecoration(new AllRecyclerViewDecoration(10));
//        textView = root.findViewById(R.id.text_all);
//        textView.setText("여기는 전체사진");
//        final TextView textView = root.findViewById(R.id.text_home);
//        allViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }

    private void showObjects(final int position) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(position));
        Log.e("ITEM_CLICK", position + " " + filePaths.get(position));
        firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);

        labeler.processImage(firebaseImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                        String message = "";
                        for (FirebaseVisionImageLabel label : firebaseVisionImageLabels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            String confidence = Float.toString(label.getConfidence());
                            message += text + " " + entityId + " " + confidence + "\n";

                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(filePaths.get(position)).setMessage(message);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to objectDetection", Toast.LENGTH_LONG).show();
                    }
                });
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