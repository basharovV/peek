package com.peekapps.peek.activities;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.peekapps.peek.DisplayMarkersTask;
import com.peekapps.peek.PermissionActions;
import com.peekapps.peek.adapters.TextFocusPagerAdapter;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.place_api.PlacesFetchedEvent;
import com.peekapps.peek.place_api.PlacesListener;
import com.peekapps.peek.fragments.CameraFragment;
import com.peekapps.peek.fragments.FeedFragment;
import com.peekapps.peek.fragments.MapFragment;
import com.peekapps.peek.R;
import com.peekapps.peek.place_api.PlacesTask;
import com.peekapps.peek.views.OnAreaSelectorReadyListener;
import com.peekapps.peek.views.TextFocusViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Slav on 25/05/2015.
 */

public class PeekViewPager extends AppCompatActivity implements OnMapReadyCallback, PlacesListener,
        LocationListener, OnAreaSelectorReadyListener {

    private View decorView;

    boolean allGranted = false;

    private ViewPager viewPager;
    private ScreenPagerAdapter viewPagerAdapter;
    private Object currentPosition;

    private LinearLayout toolbarGroup;
    private Toolbar toolbar;
    private TextFocusViewPager areaSelectorPager;
    private TextFocusPagerAdapter areaSelectorAdapter;
    private ImageButton createEventButton;
    private ImageView overflowButton;

    private BottomSheet bottomSheet;

    private Fragment[] fragments;
    private static final int N_OF_FRAGMENTS = 3;

    private List<HashMap<String, String>> placesList;
    private GoogleMap googleMap;

    private LocationManager locationManager;

    //Use superclass constructor
    public PeekViewPager() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        startBasic();

        if (Build.VERSION.SDK_INT >= 23) {
            final ArrayList<String> missingPermissions = PermissionActions.getMissingPermissions(this);
            //Request permissions (if any)
            if (missingPermissions.isEmpty()) {
                allGranted = true;
                startLocationWithPermission();
            }
            else {
                Snackbar.make(findViewById(R.id.pagerFrame),
                        R.string.pager_perm_snackbar, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.pager_perm_action,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        PermissionActions.
                                                requestMissingPermissions(PeekViewPager.this, missingPermissions);
                                    }
                                }
                        )
                        .show();
            }
             //Enable features after fragments are loaded
        }
        else    startLocationWithPermission();
    }

    public boolean allPermissionsGranted() {
        return allGranted;
    }

    private void startBasic() {
        setUpUI();
    }

    private void startLocationWithPermission() {
        //Start listening for location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) {
            Log.d("PeekViewPager" , "security exception");
            e.printStackTrace();
        }
        PlaceActions.getInstance().setMockLocation(this); //Demo only - set location to Times Square
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int grantedCount = 0;
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 0 && grantResults.length > 0)
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allGranted = false;
                }
                else grantedCount++;
            }
        if (grantedCount == grantResults.length) notifyPermissionListeners();
    }

    private void notifyPermissionListeners() {
        try {
            ((MapFragment) fragments[0]).onPermissionsGranted();
            ((FeedFragment) fragments[2]).onPermissionsGranted();
            ((CameraFragment) fragments[1]).onPermissionsGranted();
        }
        catch (NullPointerException e) {
            Log.d("PeekViewPager", "Null fragment");
            e.printStackTrace();
        }
    }


    private void setUpPager() {
        //Initialise the pager and pager adapter
        viewPager = (ViewPager) findViewById(R.id.pager);
        //Empty list of fragments to begin with
        fragments = new Fragment[N_OF_FRAGMENTS];

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ScreenPagerAdapter(fragmentManager);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setUpUI() {
        //Hide the status bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setUpPager();
        setupToolbar();
    }

    private void setupToolbar() {
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

        //Area selector view pager setup
        areaSelectorPager = (TextFocusViewPager) findViewById(R.id.areaSelectorViewPager);
        areaSelectorAdapter = new TextFocusPagerAdapter(getSupportFragmentManager());
        areaSelectorAdapter.setOnReadyListener(this);
        areaSelectorPager.setAdapter(areaSelectorAdapter);



        overflowButton = (ImageView) findViewById(R.id.overflowButton);
        overflowButton.setOnClickListener(new OverflowClickListener());
//
//        createEventButton = (ImageButton) findViewById(R.id.createEventButton);
//        createEventButton.setOnClickListener(new createEventListener());

    }

    @Override
    public void onSelectorReady() {
        //Set text (testing)
        areaSelectorAdapter.setFragmentText(0, "World");
        areaSelectorAdapter.setFragmentText(1, "NY");
        areaSelectorAdapter.setFragmentText(2, "New York");
        areaSelectorAdapter.setFragmentText(3, "My area");
    }

    private class OverflowClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            launchSettings();
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
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new DisplayMarkersTask(PeekViewPager.this, googleMap).run();
            }
        });
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
        PlacesTask placesTask = new PlacesTask();
        placesTask.attachListener(this);
        placesTask.execute();
        stopLocationListening();
    }

    private void stopLocationListening() {
        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d("PeekViewPager" , "security exception");
            e.printStackTrace();
        }
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
        private float previousOffset = 2;
        private int currentPage = 0;

        public ScrollListener() {
            super();
            toolbar.measure(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            toolbarHeight = toolbarGroup.getHeight();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            switch (currentPage) {
                case 0:
//                    toolbarGroup.setY(-toolbarHeight * (positionOffset));
//                    toolbarGroup.setAlpha(1 - positionOffset);
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
//                            toolbarGroup.setAlpha(1 - positionOffset);
//                            toolbarGroup.setTranslationY(-toolbarHeight * (positionOffset));
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
//                            toolbarGroup.setAlpha(1.0f - positionOffset);
//                            toolbarGroup.setTranslationY(-toolbarHeight * positionOffset);
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
        }

        //Handle status bar here (when page is selected, scroll not finished)
        public void onPageSelected(int position) {
            currentPage = position;
            switch (position) {
                case 0:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    break;
                case 1:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    toolbarGroup.setAlpha(0x0);
                    break;
                case 2:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    toolbarGroup.setAlpha(0x1);
                    break;
            }
        }


        //Handle camera preview here (after scroll has stopped)
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                currentPage = viewPager.getCurrentItem();
            }
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                switch (currentPage) {
                    case 0:
                        //Stop the camera preview
                        if (fragments[1] != null) {
                            ((CameraFragment) fragments[1]).stop();
                        }
                        break;
                    case 1:
                        //Start the camera preview
                        if (fragments[1] != null) {
                            ((CameraFragment) fragments[1]).start();
                        }
                        toolbarGroup.setAlpha(0x0);
                        break;
                    case 2:
                        //Stop the camera preview
                        if (fragments[1] != null) {
                            ((CameraFragment) fragments[1]).stop();
                        }
                        toolbarGroup.setTranslationY(0);
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
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    fragments[0] = new MapFragment();
                    return fragments[0];
                case 1:
                    fragments[1] = new CameraFragment();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragmentIndex", viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.get("fragmentIndex");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        if (currentPosition != null) {
            viewPager.setCurrentItem((int) currentPosition);
        }
        else {
            viewPager.setCurrentItem(1);
            viewPager.setOffscreenPageLimit(2);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}

