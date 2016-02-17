package com.peekapps.peek.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.peekapps.peek.Animations;
import com.peekapps.peek.PermissionActions;
import com.peekapps.peek.adapters.TextFocusPagerAdapter;
import com.peekapps.peek.place_api.Place;
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

/**
 * Created by Slav on 25/05/2015.
 */

public class PeekViewPager extends AppCompatActivity implements OnMapReadyCallback, PlacesListener,
        LocationListener, OnAreaSelectorReadyListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private View decorView;

    private boolean requestedUpdates = false;
    private static final int TOPINFO_LOC_IN_PROGRESS = R.string.top_info_wait;
    private static final int TOPINFO_LOC_DONE = R.string.top_info_done;

    // Top info - waitin for location
    private int currentTopInfo = TOPINFO_LOC_IN_PROGRESS;
    private FrameLayout topInfoBar;
    private View topInfoBarProgress;
    private View topInfoBarDone;
    private TextView topInfoBarText;

    //Startup, waiting for location dialog
    private ProgressDialog startDialog;
    //Flag to indicate all permissions granted (for Android 6.0+)
    boolean allGranted = false;

    //Pager attributes
    private ViewPager viewPager;
    private ScreenPagerAdapter viewPagerAdapter;
    private Object currentPosition;

    private static final int N_OF_FRAGMENTS = 3;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>(N_OF_FRAGMENTS);


    //Toolbar attributes
    private LinearLayout toolbarGroup;
    private Toolbar toolbar;
    private TextFocusViewPager areaSelectorPager;
    private TextFocusPagerAdapter areaSelectorAdapter;
    private ImageButton createEventButton;  //Not for current release
    private ImageView overflowButton;

    private LocationManager locationManager;
    private GoogleApiClient googleApiClient;

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
        checkInternetConnection();
    }

    private void checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {

        }
    }

    private void startLocationWithPermission() {
        showTopInfo(TOPINFO_LOC_IN_PROGRESS);
//        showDialog();
        //Start listening for location

        PlaceActions.getInstance(this).clearMockLocation(this);
        Log.d("PeekViewPager", "Clearing mock location..");
        requestedUpdates = true;
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            // MOCK New York location
//            PlaceActions.getInstance().setMockLocation(this);
//        }
//        catch (SecurityException e) {
//            Log.d("PeekViewPager" , "security exception");
//
//            e.printStackTrace();
//        }
         //Demo only - set location to Times Square

        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            googleApiClient.connect();
        }
    }

    public GoogleApiClient getApiClient() {
        return googleApiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (requestedUpdates) {
            PlaceActions.getInstance(this).requestLocationUpdates(googleApiClient, this);
            requestedUpdates = false;
        }
    }

    private void showDialog() {
        startDialog = new ProgressDialog(PeekViewPager.this, ProgressDialog.STYLE_SPINNER);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startDialog.setCancelable(false);
                startDialog.setMessage("Waiting for location...");
                startDialog.setTitle("One moment");
                startDialog.show();
            }
        });
    }

    private void hideDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (startDialog != null) {
                    startDialog.setTitle("Done!");
                    startDialog.setMessage("Location ready");
                    setProgressDrawableDone(R.drawable.ic_done);
                    //Wait 1 sec before hiding dialog
                    Handler hideDialogHandler = new Handler();
                    hideDialogHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startDialog.dismiss();
                            startDialog = null;
                        }
                    }, 1000);
                }
            }
        });
    }

    private void setProgressDrawableDone(int resId) {
        //Getting a progressBar from dialog
        ProgressBar bar = (ProgressBar) startDialog.findViewById(android.R.id.progress);
        //Getting a DONE(new) drawable from resources
        Drawable drawable = getResources().getDrawable(resId);
        //Getting a drawable from progress dialog
        Drawable indeterminateDrawable = bar.getIndeterminateDrawable();
        //Obtain a bounds of current drawable
        Rect bounds = indeterminateDrawable.getBounds();
        //Set bounds to DONE(new) drawable
        drawable.setBounds(bounds);
        //Set a new drawable
        startDialog.setIndeterminateDrawable(drawable);
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
        if (grantedCount == grantResults.length) {
            startLocationWithPermission();
            notifyPermissionListeners();
        }
    }


    private void notifyPermissionListeners() {
        try {
            ((MapFragment) registeredFragments.get(0)).onPermissionsGranted();
            ((CameraFragment) registeredFragments.get(1)).onPermissionsGranted();
            ((FeedFragment) registeredFragments.get(2)).onPermissionsGranted();
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ScreenPagerAdapter(fragmentManager);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setUpUI() {
        //Hide the status bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();
//        startDialog = new ProgressDialog(PeekViewPager.this, ProgressDialog.STYLE_SPINNER);
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setupTopInfo();
        setUpPager();
        setupToolbar();
    }

    private void setupTopInfo() {
        topInfoBar = (FrameLayout) findViewById(R.id.pagerTopInfoBar);
        topInfoBarProgress = findViewById(R.id.pagerTopInfoDarkBG);
        topInfoBarDone = findViewById(R.id.pagerTopInfoGreenBG);
        topInfoBarDone.setVisibility(View.GONE);
        topInfoBarText = (TextView) findViewById(R.id.pagerTopInfoBarText);
        topInfoBarText.setText(getString(TOPINFO_LOC_IN_PROGRESS));
    }

    private void showTopInfo(final int msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                topInfoBar.setVisibility(View.VISIBLE);
                if (topInfoBar.getAlpha() != 1) {
                    Animations.getInstance().fade(topInfoBar, Animations.ANIMATION_FADE_IN, 0);
                }
                switch (msg) {
                    case TOPINFO_LOC_IN_PROGRESS:
                        if (currentTopInfo != TOPINFO_LOC_IN_PROGRESS) {
                            topInfoBarText.setText(getResources().getString(TOPINFO_LOC_IN_PROGRESS));
                            currentTopInfo = TOPINFO_LOC_IN_PROGRESS;
                        }
                        break;
                    case TOPINFO_LOC_DONE:
                        if (currentTopInfo != TOPINFO_LOC_DONE) {
                            Animations.getInstance().fade(topInfoBarDone, Animations.ANIMATION_FADE_IN, 0);
                            topInfoBarText.setText(getResources().getString(TOPINFO_LOC_DONE));
                            currentTopInfo = TOPINFO_LOC_DONE;
                            dismissTopInfo();
                        }
                        break;

                }
            }
        });
    }

    private void dismissTopInfo() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animations.getInstance().fade(topInfoBar, Animations.ANIMATION_FADE_OUT, 0);
                    }
                });

            }
        }, 3000);
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


//
//        overflowButton = (ImageView) findViewById(R.id.overflowButton);
//        overflowButton.setOnClickListener(new OverflowClickListener());
//
//        createEventButton = (ImageButton) findViewById(R.id.createEventButton);
//        createEventButton.setOnClickListener(new createEventListener());

    }

    @Override
    public void onSelectorReady() {
        //Set text (testing)
        areaSelectorAdapter.setFragmentText(0, "World");
        areaSelectorAdapter.setFragmentText(1, "United States");
        areaSelectorAdapter.setFragmentText(2, "NY");
        areaSelectorAdapter.setFragmentText(3, "New York");
        areaSelectorAdapter.setFragmentText(4, "My area withlong text");
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
        showTopInfo(TOPINFO_LOC_DONE);
//        dismissTopInfo();
//        hideDialog();
        if (getMapFragment() == null) {
            registeredFragments.put(0, new MapFragment());
        } else {
            if((getMapFragment().hasMarkers())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMapFragment().setPlacesList();
                        getMapFragment().displayAllMarkers();
                    }
                });
            }
        }
        if (getFeedFragment() == null) {
            registeredFragments.put(2, new FeedFragment());
        }
        else {
            getFeedFragment().updateFeed();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (getMapFragment() != null) {
            if (!(getMapFragment().hasMarkers())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMapFragment().setPlacesList();
                        getMapFragment().displayAllMarkers();
                    }
                });
            }
        }
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
        if (getMapFragment() == null) {
            registeredFragments.put(0, new MapFragment());
        }
        ((MapFragment) registeredFragments.get(0)).enableLocation();

        PlaceActions.getInstance(this).setCurrentLocation(location);

        //Get Google nearby places
        PlacesTask placesTask = new PlacesTask();
        placesTask.attachListener(this);
        Object toPass[] = new Object[1];
        toPass[0] = location;
        placesTask.execute(toPass);
        stopLocationListening();
    }

    private void stopLocationListening() {
        try {
//            locationManager.removeUpdates(this);
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
            currentPosition = position;
            switch (position) {
                case 0:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
                        if (getCameraFragment() != null) {
                            (getCameraFragment()).stop();
                        }
                        break;
                    case 1:
                        //Start the camera preview
                        if (getCameraFragment() != null) {
                            ((CameraFragment) getCameraFragment()).start();
                        }
                        toolbarGroup.setAlpha(0x0);
                        break;
                    case 2:
                        //Stop the camera preview
                        if (getCameraFragment() != null) {
                            ((CameraFragment) getCameraFragment()).stop();
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
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            registeredFragments.remove(position);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapFragment();
                case 1:
                    return new CameraFragment();
                case 2:
                    return new FeedFragment();
                default:
                    return new CameraFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public MapFragment getMapFragment() {
        return (MapFragment) registeredFragments.get(0);
    }

    public CameraFragment getCameraFragment() {
        return (CameraFragment) registeredFragments.get(1);
    }
    public FeedFragment getFeedFragment() {
        return (FeedFragment) registeredFragments.get(2);
    }

    private void launchCreateEvent() {
            Intent createEventIntent = new Intent(PeekViewPager.this, CreateEventActivity.class);
            startActivity(createEventIntent);
        }
    private void launchSettings() {
        Intent settingsIntent = new Intent(PeekViewPager.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (currentPosition != null) {
            outState.putInt("fragmentIndex", (int) currentPosition);
        }
        super.onSaveInstanceState(outState);
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
//        AppEventsLogger.deactivateApp(this);
        startDialog = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        startDialog = null;
    }
}

