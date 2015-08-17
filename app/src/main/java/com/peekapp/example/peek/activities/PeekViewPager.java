package com.peekapp.example.peek.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.peekapp.example.peek.DisplayMarkersTask;
import com.peekapp.example.peek.place_api.PlacesFetchedEvent;
import com.peekapp.example.peek.place_api.PlacesListener;
import com.peekapp.example.peek.fragments.CameraFragment;
import com.peekapp.example.peek.fragments.FeedFragment;
import com.peekapp.example.peek.fragments.MapFragment;
import com.peekapp.example.peek.R;
import com.peekapp.example.peek.place_api.PlacesTask;

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
            bottomSheet = new BottomSheet.Builder(PeekViewPager.this)
                    .title("Options")
                    .sheet(R.menu.bottom_options)
                    .listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),
                                    "Clicked " + which, Toast.LENGTH_LONG).show();
                        }
                    }).show();
            overflowButton.setColorFilter(0xCCFFFFFF, PorterDuff.Mode.MULTIPLY);

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
    public class ScrollListener implements ViewPager.OnPageChangeListener {
        //Hold the height to change the y-offset on user scroll
        private int toolbarHeight;
        private int viewPagerHeight;

        /**
         * Scroll direction:
         * 0 - left | 1 - stationary |2 - right
         */
        private int scrollDir;

        public ScrollListener() {
            super();
            toolbar.measure(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            toolbarHeight = toolbarGroup.getHeight();
            viewPagerHeight = viewPager.getHeight();
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //Set scrolling/swiping direction
            if (positionOffset < 0.5) {
                scrollDir = 0; //Left
            }
            else if (positionOffset == 0.5) {
                scrollDir = 1; //Stationary
            }
            else {
                scrollDir = 2; //Right
            }

            if (position == 1) {
                //Decrease transparency
                toolbarGroup.setTranslationY(toolbar.getTranslationY() * positionOffset);
                toolbarGroup.setAlpha(positionOffset);
                //Move toolbar down
            }
            else {
                //Increase transparency
                toolbarGroup.setTranslationY(-toolbarHeight * positionOffset);
                toolbarGroup.setAlpha(1 - positionOffset);
                //Move toolbar up
            }
        }

        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    toolbarGroup.setAlpha(1);
                    toolbarGroup.setTranslationY(0);
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).stop();
                    }
                    break;
                case 1:
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).start();
                    }
                    toolbarGroup.setAlpha(0);
                    toolbarGroup.setTranslationY(-toolbarHeight);
                    break;
                case 2:
                    if (fragments[1] != null) {
                        ((CameraFragment) fragments[1]).stop();
                    }
                    toolbarGroup.setAlpha(1);
                    toolbarGroup.setTranslationY(0);
                    break;
                }
        }


        public void onPageScrollStateChanged(int state) {

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

    private class createEventListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent createEventIntent = new Intent(PeekViewPager.this, CreateEventActivity.class);
            startActivity(createEventIntent);
        }
    }
}

