package com.example.galleryproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {
    private ImageButton photo_backButton;
    private ImageView photo_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent(); /*데이터 수신*/
        File file = intent.getExtras().getParcelable("file");

        photo_backButton = (ImageButton) findViewById(R.id.photo_backButton);
        photo_backButton.setOnClickListener((view) -> {
            finish();
        });

        photo_imageView = (ImageView) findViewById(R.id.photo_imageView);
//        Log.e("PHOTO_Activity", filePath);
//        Bitmap bitmap = BitmapFactory.decodeFile(new File(filePath).getAbsolutePath());
//        photo_imageView.setImageBitmap(bitmap);
        Glide.with(this.getApplicationContext())
                .load(file)
                .into(photo_imageView);
    }
}
