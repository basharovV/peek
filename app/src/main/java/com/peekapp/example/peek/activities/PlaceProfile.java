package com.peekapp.example.peek.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.peekapp.example.peek.adapters.ProfileCardAdapter;
import com.peekapp.example.peek.place_api.Place;
import com.peekapp.example.peek.R;

import java.io.File;

/**
 * Created by Slav on 11/06/2015.
 */
public class PlaceProfile extends Activity {

    //User interface elements
    private Toolbar toolbar;
    private ImageButton backButton;
    private TextView nameView;
    private TextView vicinityView;
    private ImageView photoView;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //Place object
    private Place place;

    //Place properties
    private String placeName;
    private String placeVicinity;
    private String placePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_profile);
        Intent intent = getIntent();
        place = (Place) intent.getSerializableExtra("place_object");

        //Get UI views from layout
        toolbar = (Toolbar) findViewById(R.id.pl_profile_toolbar);
//        nameView = (TextView) findViewById(R.id.plProfileName);
//        vicinityView = (TextView) findViewById(R.id.plProfileVic);
        photoView = (ImageView) findViewById(R.id.plProfilePhoto);

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.plProfileRecView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ProfileCardAdapter());

        //TOOLBAR BACK BUTTON
//        backButton = (ImageButton) findViewById(R.id.profileBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        populateUI();
    }

    private void populateUI() {
        //Set instance variables
        placeName = place.getName();
        placeVicinity = place.getVicinity();
        ImageLoader imageLoader = ImageLoader.getInstance();

        String path = getExternalCacheDir() + "/" + place.getID() + "photo.jpg";
        File photo = new File(path);
        if (photo.exists()) {
            DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
            imageLoader.displayImage("file://" + path, photoView, displayOptions);
        }
        //Populate
        nameView.setText(placeName);
        vicinityView.setText(placeVicinity);

    }








}
