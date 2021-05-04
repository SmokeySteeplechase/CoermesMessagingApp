package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class imageDisplay extends AppCompatActivity {
    public ImageView DisplayPic;
    public String imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        DisplayPic =findViewById(R.id.imageView3);
        imageURL = getIntent().getStringExtra("url");
        Picasso.get().load(imageURL).into(DisplayPic);
    }
}