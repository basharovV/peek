package com.peekapps.peek.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.peekapps.peek.DisplayMarkersTask;
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.place_api.PlacesFetchedEvent;
import com.peekapps.peek.place_api.PlacesListener;
import com.peekapps.peek.fragments.CameraFragment;
import com.peekapps.peek.fragments.FeedFragment;
import com.peekapps.peek.fragments.MapFragment;
import com.peekapps.peek.R;
import com.peekapps.peek.place_api.PlacesTask;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Slav on 25/05/2015.
 */

public class PeekViewPager extends AppCompatActivity implements OnMapReadyCallback, PlacesListener, LocationListener{

    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private LinearLayout toolbarGroup;
    private Toolbar toolbar;
    private ImageButton createEventButton;
    private ImageView overflowButton;

    private BottomSheet bottomSheet;

    private Fragment[] fragments;
    private static final int N_OF_FRAGMENTS = 3;

    private List<HashMap<String, String>> placesList;
    private GoogleMap googleMap;

    private MaterialDialog progressDialog;
    private LocationManager locationManager;

    //Use superclass constructor
    public PeekViewPager() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        setUpPager();
        setUpUI();

        //Show loading dialog white fetching places
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.load_dialog_title)
                .content(R.string.load_dialog_content)
                .progress(true, 0)
                .show();

        //Start listening for location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        PlaceActions.getInstance().setMockLocation(this); //Demo only - set location to Times Square

    }


    private void setUpPager() {
        //Initialise the pager and pager adapter
        viewPager = (ViewPager) findViewById(R.id.pager);
        //Empty list of fragments to begin with
        fragments = new Fragment[N_OF_FRAGMENTS];

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ScreenPagerAdapter(fragmentManager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);
    }

    private void setUpUI() {
        //Initialise the toolbar
        toolbar = (Toolbar) findViewById(R.id.peek_toolbar);
        toolbarGroup = (LinearLayout) findViewById(R.id.peek_toolbar_group);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarGroup.setAlpha(0);
                toolbar.setTitle("");
            }
        }

        //Add OnPageChangeListener after toolbar is drawn (onSizeChanged)
        ViewTreeObserver viewTreeObserver = toolbarGroup.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    toolbarGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    toolbarGroup.setTranslationY(-toolbar.getHeight());
                    viewPager.setOnPageChangeListener(new ScrollListener());
                }
            });
        }

        overflowButton = (ImageView) findViewById(R.id.overflowButton);
        overflowButton.setOnClickListener(new OverflowClickListener());
//
//        createEventButton = (ImageButton) findViewById(R.id.createEventButton);
//        createEventButton.setOnClickListener(new createEventListener());

    }


    private class OverflowClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            launchSettings();
            //Not yet
//            bottomSheet = new BottomSheet.Builder(PeekViewPager.this)
//                    .title("Options")
//                    .sheet(R.menu.bottom_options)
//                    .listener(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case R.id.createEventItem:
//                                    launchCreateEvent();
//                                    break;
//                                case R.id.settings_item:
//                                    launchSettings();
//                                    break;
//                                case R.id.profileItem:
//                                    launchProfile();
//                                    break;
//                            }
//                            Toast.makeText(getApplicationContext(),
//                                    "Clicked " + which, Toast.LENGTH_LONG).show();
//                        }
//                    }).show();
//            overflowButton.setColorFilter(0xCCFFFFFF, PorterDuff.Mode.MULTIPLY);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onPlacesFetched(PlacesFetchedEvent e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new DisplayMarkersTask(PeekViewPager.this, googleMap).run();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }


    /**
     * Location listener interface methods
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        progressDialog.setTitle(R.string.load_dialog_done);
        progressDialog.setContent(R.string.load_dialog_done_content);
        progressDialog.cancel();

        PlacesTask placesTask = new PlacesTask();
        placesTask.attachListener(this);
        placesTask.execute();

        ((MapFragment) fragments[0]).updateLocation(location);
        stopLocationListening();
    }

    private void stopLocationListening() {
        locationManager.removeUpdates(this);
    }
    /**
     * Listener for user scrolling the ViewPager - animate toolbar accordingly
     *  If current fragment is 'Camera', animate toolbar 'in'
     *  Otherwise, animate toolbar 'out'
     * The animation consists of a fade and 'slide in' effect.
     */
    public class ScrollListener implements OnPageChangeListener {
        //Hold the height to change the y-offset on user scroll
        private int toolbarHeight;
        private int viewPagerHeight;

        /**
         * Scroll direction:
         * 0 - left | 1 - stationary |2 - right
         */
        private float previousOffset = 2;
        private int scrollDir = 0;
        private int currentPage = 0;

        public ScrollListener() {
            super();
            toolbar.measure(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            toolbarHeight = toolbarGroup.getHeight();
            viewPagerHeight = viewPager.getHeight();
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            switch (currentPage) {
                case 0:
                    toolbarGroup.setY(-toolbarHeight * (positionOffset));
                    toolbarGroup.setAlpha(1 - positionOffset);
                    break;
                case 1:
                    //If not been initialised
                    if (previousOffset == 2) {
                        previousOffset = positionOffset;
                    }
                    //...moving to the LEFT
                    if (previousOffset > positionOffset) {
                        //towards MAP
                        if (position == 0) {
                            toolbarGroup.setAlpha(1 - positionOffset);
                            toolbarGroup.setTranslationY(-toolbarHeight * (positionOffset));
                        }
                        //towards CAMERA
                        else {
                            toolbarGroup.setAlpha(positionOffset);
                            toolbarGroup.setTranslationY(-toolbarHeight * (1.0f - positionOffset));
                        }
                    }
                    //...moving to the RIGHT
                    else if (previousOffset < positionOffset) {
                        //towards FEED
                        if (position == 1) {
                            toolbarGroup.setAlpha(positionOffset);
                            toolbarGroup.setTranslationY(-toolbarHeight * (1.0f - positionOffset));
                        }
                        //towards CAMERA
                        else {
                            toolbarGroup.setAlpha(1.0f - positionOffset);
                            toolbarGroup.setTranslationY(-toolbarHeight * positionOffset);
                        }
                    }
                    previousOffset = positionOffset;
                    break;
                case 2:
                    if (positionOffset == 0) toolbarGroup.setAlpha(1.0f);
                    else {
                        toolbarGroup.setAlpha(positionOffset);
                        toolbarGroup.setTranslationY(-toolbarHeight * (1 - positionOffset));
                    }
                    break;
            }
//            Log.d("ViewPager", "Alpha = " + toolbarGroup.getAlpha()
//                    + " | Offset =" + positionOffset + " | Position = " + position
//                    + " | Current page = " + currentPage);

        }

        public void onPageSelected(int position) {
            currentPage = position;
            switch (position) {
                case 0:
                    toolbarGroup.setAlpha(0x1);
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).stop();
                    }
                    break;
                case 1:
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).start();
                    }
                    toolbarGroup.setAlpha(0x0);
                    break;
                case 2:
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).stop();
                    }
                    toolbarGroup.setAlpha(0x1);
                    break;
            }
        }


        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                currentPage = viewPager.getCurrentItem();
            }
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                switch (currentPage) {
                    case 0:
                        toolbarGroup.setAlpha(0x1);
                        break;
                    case 1:
                        toolbarGroup.setAlpha(0x0);
                        break;
                    case 2:
                        toolbarGroup.setAlpha(0x1);
                        break;
                }
            }
        }
    }

    public class ScreenPagerAdapter extends FragmentStatePagerAdapter {


        public ScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (fragments[0] == null) {
                        fragments[0] = new MapFragment();
                    }
                    return fragments[0];
                case 1:
                    if (fragments[1] == null) {
                        fragments[1] = new CameraFragment();
                    }
                    return fragments[1];
                case 2:
                    if (fragments[2] == null) {
                        fragments[2] = new FeedFragment();
                    }
                    return fragments[2];
                default:
                    if (fragments[1] == null) {
                        fragments[1] = new CameraFragment();
                    }
                    return fragments[1];
            }
        }


        @Override
        public int getCount() {
            return 3;
        }
    }

    private void launchCreateEvent() {
            Intent createEventIntent = new Intent(PeekViewPager.this, CreateEventActivity.class);
            startActivity(createEventIntent);
        }
    private void launchSettings() {
        Intent settingsIntent = new Intent(PeekViewPager.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void launchProfile() {
        Intent createEventIntent = new Intent(PeekViewPager.this, ProfileActivity.class);
        startActivity(createEventIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}

