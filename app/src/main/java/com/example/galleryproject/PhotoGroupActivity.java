package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
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
import com.example.galleryproject.ui.all.AllRecyclerViewDecoration;
import com.example.galleryproject.Model.ImageGroup;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PhotoGroupActivity extends AppCompatActivity {
    private TextView photoGroup_date_textView;
    private TextView photoGroup_Memo_textView;
    private EditText photoGroup_Memo_editText;
    private ImageButton saveButton;
    private ImageButton photoGroup_backButton;

    private InputMethodManager imm;

    private Adapter adapter;
    private RecyclerView photoGroup_RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();
        ImageGroup imageGroup = bundle.getParcelable("ImageGroup");

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        saveButton = findViewById(R.id.saveButton);
        photoGroup_backButton = findViewById(R.id.photoGroup_backButton);
        photoGroup_date_textView = findViewById(R.id.photoGroup_date_textView);
        photoGroup_Memo_editText = findViewById(R.id.photoGroup_Memo_editText);
        photoGroup_Memo_textView = findViewById(R.id.photoGroup_Memo_textView);

        photoGroup_date_textView.setText(imageGroup.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        photoGroup_Memo_textView.setText(imageGroup.getMemo());
        photoGroup_Memo_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoGroup_Memo_textView.setVisibility(View.GONE);
                photoGroup_Memo_editText.setVisibility(View.VISIBLE);
                photoGroup_Memo_editText.setText(photoGroup_Memo_textView.getText());
                photoGroup_Memo_editText.setSelection(photoGroup_Memo_textView.getText().length());
                photoGroup_Memo_editText.requestFocus();
                imm.showSoftInput(photoGroup_Memo_editText,0);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoGroup_Memo_editText.setVisibility(View.GONE);
                photoGroup_Memo_textView.setVisibility(View.VISIBLE);
                photoGroup_Memo_textView.setText(photoGroup_Memo_editText.getText());
                photoGroup_Memo_editText.clearFocus();
                imm.hideSoftInputFromWindow(photoGroup_Memo_editText.getWindowToken(), 0);
                saveButton.setVisibility(View.GONE);
                //TODO 수정한 메모 저장
            }
        });

        photoGroup_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        photoGroup_RecyclerView = findViewById(R.id.photoGroup_RecyclerView);
        photoGroup_RecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new Adapter(imageGroup.getFilePaths());
        photoGroup_RecyclerView.setAdapter(adapter);
        photoGroup_RecyclerView.addItemDecoration(new AllRecyclerViewDecoration(10));
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        private List<String> filePaths;

        public Adapter(List<String> filePaths){
            this.filePaths = filePaths;
//            for(String filepath : filePaths)
//                Log.e("Adapter : ", filepath);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView) ;
                imageView = (ImageView) itemView.findViewById(R.id.photoGroupItem_ImageView);
            }
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.photogroup_item, parent, false) ;
            Adapter.ViewHolder vh = new Adapter.ViewHolder(view) ;

            return vh ;
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            Glide.with(getApplicationContext())
                    .load(new File(filePaths.get(position)))
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return filePaths.size();
        }
    }

}
