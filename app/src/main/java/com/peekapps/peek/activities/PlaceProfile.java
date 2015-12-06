package com.peekapps.peek.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.peekapps.peek.adapters.ProfileCardAdapter;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Slav on 11/06/2015.
 */
public class PlaceProfile extends AppCompatActivity {

    private static final int TIMEFILTER_TODAY = 0;
    private static final int TIMEFILTER_WEEK = 1;

    //-------------DEFAULT: today---------------
    private int timeFilter = TIMEFILTER_TODAY;

    //User interface elements
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;

    //Buttons - header
    private ImageView backButton;
    private ImageView todayButton;
    private ImageView weekButton;
    private ImageView mapButton;
    private ImageView searchButton;

    //Buttons - bottom
    private TextView worldButton;
    private TextView myUploadsButton;
    private TextView friendsButton;

    //Header info
    private TextView nameView;
    private TextView vicinityView;
    private TextView typeView;
    private KenBurnsView photoView;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //Place object to display
    private Place place;

    //Place properties
    private String placeName;
    private String placeVicinity;
    private String placeType;
    private String placePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_profile);
        Intent intent = getIntent();

        //Set instance variables
        place = (Place) intent.getSerializableExtra("place_object");
        placeName = place.getName();
        placeVicinity = place.getVicinity();
        placeType = place.getType();

        //Get UI views from layout
        toolbar = (Toolbar) findViewById(R.id.plProfileToolbar);

        todayButton = (ImageView) findViewById(R.id.plProfileTodayButton);
        weekButton = (ImageView) findViewById(R.id.plProfileWeekButton);

        nameView = (TextView) findViewById(R.id.plProfileName);
        vicinityView = (TextView) findViewById(R.id.plProfileVic);
        typeView = (TextView) findViewById(R.id.plProfileType);
        photoView = (KenBurnsView) findViewById(R.id.plProfilePhoto);

        setUpButtons();

        setSupportActionBar(toolbar);


        layoutManager = new LinearLayoutManager(this);

        //OLD!
//        recyclerView = (RecyclerView) findViewById(R.id.plProfileRecView);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(new ProfileCardAdapter());

        //TOOLBAR BACK BUTTON
        backButton = (ImageView) findViewById(R.id.plProfileBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        populateUI();
    }

    private void setUpButtons() {
        OnTimeFilterClickListener timeFilterClickListener = new OnTimeFilterClickListener();
        todayButton.setOnClickListener(timeFilterClickListener);
        weekButton.setOnClickListener(timeFilterClickListener);
    }

    private void populateUI() {

        String path = getExternalCacheDir() + "/" + place.getID() + "photo.jpg";
        File photo = new File(path);
        if (photo.exists()) {
            Picasso.with(this)
                    .load("file://" + path)
                    .fit()
                    .centerCrop()
                    .into(photoView);
        }
        //Populate
        nameView.setText(placeName);
        vicinityView.setText(placeVicinity);
        typeView.setText(placeType);
    }

    public class OnTimeFilterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == weekButton) {
                if (timeFilter == TIMEFILTER_TODAY) {
                    switchTimeFilterTo(TIMEFILTER_WEEK);
                }
            }
            else if (v == todayButton) {
                if (timeFilter == TIMEFILTER_WEEK) {
                    switchTimeFilterTo(TIMEFILTER_TODAY);
                }
            }
        }
    }

    private void switchTimeFilterTo(int mode) {
        switch (mode) {
            case TIMEFILTER_TODAY:
                todayButton.setImageResource(R.drawable.profile_today_pressed);
                weekButton.setImageResource(R.drawable.profile_week_unpressed);
                timeFilter = TIMEFILTER_TODAY;
                break;
            case TIMEFILTER_WEEK:
                todayButton.setImageResource(R.drawable.profile_today_unpressed);
                weekButton.setImageResource(R.drawable.profile_week_pressed);
                timeFilter = TIMEFILTER_WEEK;
                break;
        }
    }








}
