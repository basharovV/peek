package com.peekapps.peek.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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

    private SparseArray<TextView> contentModeButtons;
    private ContentModeChangeListener clickListener;

    //Header info
    private TextView nameView;
    private TextView vicinityView;
    private TextView typeView;
    private ImageView uploadCountView;
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

    private static final int CONTENT_MODE_MY_UPLOADS = 3;
    private static final int CONTENT_MODE_WORLD = 4;
    private static final int CONTENT_MODE_FRIENDS = 5;

    private int contentMode = CONTENT_MODE_WORLD;



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
        uploadCountView = (ImageView) findViewById(R.id.uploadCountIcon);

        contentModeButtons = new SparseArray<>();
        setUpUI();
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
                supportFinishAfterTransition();
            }
        });
        populateUI();
    }

    private void setUpUI() {
        uploadCountView.setColorFilter(ContextCompat.getColor(this, R.color.peek_orange_logo));
    }
    private void setUpButtons() {
        OnTimeFilterClickListener timeFilterClickListener = new OnTimeFilterClickListener();
        todayButton.setOnClickListener(timeFilterClickListener);
        weekButton.setOnClickListener(timeFilterClickListener);

        myUploadsButton = (TextView) findViewById(R.id.plProfileMyUploads);
        worldButton = (TextView) findViewById(R.id.plProfileWorld);
        friendsButton = (TextView) findViewById(R.id.plProfileFriends);

        contentModeButtons.put(0, myUploadsButton);
        contentModeButtons.put(1, worldButton);
        contentModeButtons.put(2, friendsButton);

        clickListener = new ContentModeChangeListener();

        for (int i = 0; i < contentModeButtons.size(); i++) {
            contentModeButtons.get(i).setOnClickListener(clickListener);
        }

        myUploadsButton.setTextColor(getResources().getColor(R.color.peek_inactive));
        worldButton.setTextColor(getResources().getColor(R.color.peek_orange));
        friendsButton.setTextColor(getResources().getColor(R.color.peek_inactive));

    }

    public class ContentModeChangeListener implements View.OnClickListener {

        public void onClick(View v) {
            for (int i = 0; i < contentModeButtons.size(); i++) {
                contentModeButtons.get(i).setTextAppearance(getApplicationContext(), R.style.PlProfileUploaderType);
            }
            ((TextView) v).setTextAppearance(getApplicationContext(), R.style.PlProfileUploaderTypePressed);
        }
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
