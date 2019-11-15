package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.ui.all.AllRecyclerViewDecoration;
import com.example.galleryproject.Model.ImageGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhotoGroupActivity extends AppCompatActivity {
    private TextView photoGroup_date_textView;
    private TextView photoGroup_Memo_textView;
    private EditText photoGroup_Memo_editText;
    private Button saveButton;
    private ImageButton photoGroup_backButton;

    private InputMethodManager imm;

    private Adapter adapter;
    private RecyclerView photoGroup_RecyclerView;

    private ImageCollection imageCollection;
    private List<ImageGroup> imageGroups;
    private List<Boolean> selected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();
        imageCollection = bundle.getParcelable("ImageCollection");
        imageGroups = imageCollection.getGroups();

        // image list flattening
        List<Image> images = imageGroups.stream()
                            .map(ImageGroup::getImages)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

        selected = imageGroups.stream()
                            .map(x -> false)
                            .collect(Collectors.toList());

        List<File> files = images.stream()
                                 .map(Image::getFile)
                                 .collect(Collectors.toList());


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        saveButton = findViewById(R.id.saveButton);
        photoGroup_backButton = findViewById(R.id.photoGroup_backButton);
        photoGroup_date_textView = findViewById(R.id.photoGroup_date_textView);
        photoGroup_Memo_editText = findViewById(R.id.photoGroup_Memo_editText);
        photoGroup_Memo_textView = findViewById(R.id.photoGroup_Memo_textView);

        photoGroup_date_textView.setText(imageCollection.getDate().toString());
        photoGroup_Memo_textView.setText(imageCollection.getMemo());

        photoGroup_Memo_textView.setOnClickListener((view) -> {
            photoGroup_Memo_textView.setVisibility(View.GONE);
            photoGroup_Memo_editText.setVisibility(View.VISIBLE);
            photoGroup_Memo_editText.setText(photoGroup_Memo_textView.getText());
            photoGroup_Memo_editText.setSelection(photoGroup_Memo_textView.getText().length());
            photoGroup_Memo_editText.requestFocus();
            imm.showSoftInput(photoGroup_Memo_editText,0);
            saveButton.setVisibility(View.VISIBLE);
        });

        saveButton.setOnClickListener((view) -> {
            photoGroup_Memo_editText.setVisibility(View.GONE);
            photoGroup_Memo_textView.setVisibility(View.VISIBLE);
            photoGroup_Memo_textView.setText(photoGroup_Memo_editText.getText());
            photoGroup_Memo_editText.clearFocus();
            imm.hideSoftInputFromWindow(photoGroup_Memo_editText.getWindowToken(), 0);
            saveButton.setVisibility(View.GONE);
            //TODO 수정한 메모 저장
        });

        photoGroup_backButton.setOnClickListener((view) -> finish());

        photoGroup_RecyclerView = findViewById(R.id.photoGroup_RecyclerView);
        photoGroup_RecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new Adapter(images, (image) -> {

            List<Boolean> nowSelected = imageGroups.stream()
                                                    .map(x -> x.getImages().contains(image))
                                                    .collect(Collectors.toList());

            StringBuilder msg1 = new StringBuilder();
            for (Boolean b: nowSelected) {
                msg1.append(b)
                    .append(", ");
            }

            Log.e("PhotoGroupActivity", nowSelected.size() + "");
            Log.e("PhotoGroupActivity", msg1.toString());

            StringBuilder msg2 = new StringBuilder();
            for (Boolean b: selected) {
                msg2.append(b)
                    .append(", ");
            }

            Log.e("PhotoGroupActivity", selected.size() + "");
            Log.e("PhotoGroupActivity", msg2.toString());

            for (int i = 0; i < nowSelected.size(); i++) {
                if (nowSelected.get(i)) {
                    boolean current = selected.get(i);
                    selected.set(i, !current);

                    String currentMemo = imageCollection.getMemo();
                    imageCollection.setMemo(currentMemo + "\n/// " + i);
                    photoGroup_Memo_textView.setText(imageCollection.getMemo());
                }
            }

        });

        photoGroup_RecyclerView.setAdapter(adapter);
        photoGroup_RecyclerView.addItemDecoration(new AllRecyclerViewDecoration(10));
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        private List<Image> images;
        private OnItemClickListener listener;

        Adapter(List<Image> images, OnItemClickListener listener){
            this.images = images;
            this.listener = listener;
//            for(String filepath : filePaths)
//                Log.e("Adapter : ", filepath);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;


            ViewHolder(View itemView) {
                super(itemView) ;
                imageView = itemView.findViewById(R.id.photoGroupItem_ImageView);
            }

            void bind(final Image image, final OnItemClickListener listener) {
                imageView.setOnClickListener((v) -> listener.OnItemClick(image));
            }
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            View view = inflater.inflate(R.layout.photogroup_item, parent, false);

            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            Glide.with(getApplicationContext())
                    .load(images.get(position).getFile())
                    .into(holder.imageView);

            holder.bind(images.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(Image image);
    }
}
