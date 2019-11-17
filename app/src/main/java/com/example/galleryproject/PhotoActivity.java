package com.example.galleryproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.Model.Image;

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
        Image image = (Image) intent.getExtras().get("Image");

        photo_backButton = findViewById(R.id.photo_backButton);
        photo_backButton.setOnClickListener((view) -> {
            finish();
        });

        photo_imageView = findViewById(R.id.photo_imageView);
        Glide.with(this.getApplicationContext())
                .load(image.getFile())
                .into(photo_imageView);
    }
}
