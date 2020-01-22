package com.example.asus.project4th;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        PhotoView photoView = findViewById(R.id.fullScreenImageView);
        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null){
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri != null && photoView !=null){
                Glide.with(this).load(imageUri).into(photoView);
            }
        }
    }
}
