
package com.peekapps.peek.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.annotations.Marker;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
//import com.mapbox.mapboxsdk.annotations.Sprite;
//import com.mapbox.mapboxsdk.annotations.SpriteFactory;
//import com.mapbox.mapboxsdk.constants.Style;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.views.MapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.mapbox.mapboxsdk.annotations.SpriteFactory;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.adapters.PhotoPagerAdapter;
import com.peekapps.peek.database.PlaceDbHelper;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.map.MapboxTileProvider;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.place_api.PlacesListener;
import com.peekapps.peek.R;
import com.peekapps.peek.views.OnPhotoPagerReadyListener;
import com.peekapps.peek.views.PhotoPager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MapFragment extends Fragment implements OnPermissionsListener, OnPhotoPagerReadyListener {

    //Implement a observer/listeners
    private CopyOnWriteArrayList<PlacesListener> placesListeners;


    //------Views
    //Sliding panel layout
    private SlidingUpPanelLayout mapSlidingPanel;
    private MapView mapView;
    private GoogleMap googleMap;

    //Map elements
    //Mapbox SDK only
    SpriteFactory spriteFactory;

    //Panel elements
    private Toolbar panelToolbar;
    private PhotoPager photoPager;
    private PhotoPagerAdapter photoPagerAdapter;
    private TextView panelHeaderName;
    private TextView panelHeaderVic;
    private ImageView uploadCountIcon;


    private boolean markersDisplayed = false;

    private HashMap<Marker, Place> markerMap;
    private List<Place> placesList;
    private Location currentLocation;


    private int RADIUS = 6000;
    double latitude = 40.7127; //NYC coordinates
    double longitude = -74.0059;
//    double latitude = 36.5167; //Marbella coordinates
//    double longitude = -4.8833;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        if ( mapView != null ) {
            mapView.onCreate(mapViewSavedInstanceState);
        }
        markerMap = new HashMap<>();

        //Check for permission
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.
                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.
                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
//        //GoogleMapsApiV2 only
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        //Find views
        mapSlidingPanel = (SlidingUpPanelLayout) rootView.findViewById(R.id.mapFragment);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync((OnMapReadyCallback) getActivity());
        //---------MAPBOX SDK only
//        mapView.setStyleUrl(Style.MAPBOX_STREETS);
//        mapView.setAccessToken(getResources().getString(R.string.mapbox_token));
//        mapView.setZoomLevel(11);
//        mapView.onCreate(savedInstanceState);
//        spriteFactory = new SpriteFactory(mapView);

        //-----------Panel---------------
        //Header
        panelToolbar = (Toolbar) rootView.findViewById(R.id.mapPanelToolbar);
        uploadCountIcon = (ImageView) rootView.findViewById(R.id.mapPanelUploadCountIcon);
        panelHeaderName = (TextView) rootView.findViewById(R.id.mapPanelPlaceName);
        panelHeaderVic = (TextView) rootView.findViewById(R.id.mapPanelPlaceVic);

        photoPager = (PhotoPager) rootView.findViewById(R.id.mapPhotoPager);
        //Footer

        setupMap();
        setupSlidingPanel();

        if (Build.VERSION.SDK_INT >= 23) {
            if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
                enableLocation();
                //MapBox
//                mapView.setMyLocationEnabled(false);
                //GoogleMap
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            enableLocation();
            //MapBox only
//            mapView.setMyLocationEnabled(true);
            //GoogleMap
            googleMap.setMyLocationEnabled(true);
        }

        return rootView;
    }

    public void setPlacesList() {
        PlaceDbHelper dbHelper = new PlaceDbHelper(getActivity());
        placesList = dbHelper.getAllPlaces();
    }

    public boolean hasMarkers() {
        return markersDisplayed;
    }

    public void displayAllMarkers() {
        markersDisplayed = true;
        for (Place pl : placesList) {
            getActivity().runOnUiThread(new PutMarkerRunnable(pl));
        }
    }

    private void setupMap() {
        //----------MapBox-----------
//      mapView.setOnMarkerClickListener(new OnPlaceSelectedListener());
//      mapView.setOnMapClickListener(new MapView.OnMapClickListener() {
//            @Override
//            public void onMapClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng point) {
//                if (mapSlidingPanel.isEnabled()) {
//                    mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//                }
//            }
//        });

        //-----------GOOGLE MAP ---------
        googleMap = mapView.getMap();
//       // Mapbox tiles
//        MapboxTileProvider mbTileProvider = new MapboxTileProvider();
//        mbTileProvider.setAccessToken(getResources().getString(R.string.mapbox_token));
//        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mbTileProvider));

        googleMap.setOnMarkerClickListener(new OnPlaceSelectedListener());
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapSlidingPanel.isEnabled()) {
                    mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });
        googleMap.setMyLocationEnabled(true);
    }

    private void setupSlidingPanel() {
        //Main photo pager
        photoPagerAdapter = new PhotoPagerAdapter(getChildFragmentManager());
        photoPager.setAdapter(photoPagerAdapter);
        photoPagerAdapter.setOnReadyListener(this);
        //Header elements
        uploadCountIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.peek_orange_logo));

        //Init
        mapSlidingPanel.setPanelSlideListener(new MapPanelSlideListener());
        mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mapSlidingPanel.setOverlayed(true);
    }

    public class OnPlaceSelectedListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (markerMap.containsKey(marker)) {
                Place selectedPlace = markerMap.get(marker);
                if (selectedPlace != null) {
                    panelHeaderName.setText(selectedPlace.getName());
                    panelHeaderVic.setText(selectedPlace.getVicinity());
                }
            }
            if (mapSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
            //MapBox
//            mapView.setCenterCoordinate(marker.getPosition(), true);
            //GoogleMap
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            return true;
        }

//        @Override
//        public boolean onMarkerClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {
//
    }

    public void enableLocation() {
        currentLocation = PlaceActions.getInstance().getLocation(getActivity());
        //--------GOOGLE MAP----------
        CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude()))
                .zoom(5).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

        //----------MapBox-----------
//        mapView.setCenterCoordinate(new LatLng(currentLocation.getLatitude(),
//        currentLocation.getLongitude()), true);
    }

    public void updateLocation(Location location) {
        currentLocation = location;
        //GOOGLE MAP
//        CameraPosition camPosition = new CameraPosition.Builder().target
//                (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
//                .zoom(10).build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

    }

    @Override
    public void onPermissionsGranted() {
        enableLocation();
    }

    private class PutMarkerRunnable implements Runnable {
        private Place pl;

        public PutMarkerRunnable(Place pl) {
            this.pl = pl;
        }

        @Override
        public void run() {
            MarkerOptions markerOptions = new MarkerOptions();
            double latitude = pl.getLatitude();
            double longitude = pl.getLongitude();
            String placeName = pl.getName();
            String vicinity = pl.getVicinity();
            LatLng latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(generateMarkerBitmap()));
            //-----MapBox-----
//            Marker m = mapView.addMarker(markerOptions);
            Marker m = googleMap.addMarker(markerOptions);
            markerMap.put(m, pl);
            dropPinAnimation(m);
        }
    }


    public Bitmap generateMarkerBitmap() {
        Bitmap output = Bitmap.createBitmap(72,
                72, Bitmap.Config.ARGB_8888);
        Bitmap markerBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.profile2)
                , 72, 72, false);
        Canvas markerCanvas = new Canvas(output);
        Rect rect = new Rect(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        markerCanvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        markerCanvas.drawCircle(markerBitmap.getWidth() / 2, markerBitmap.getHeight() / 2,
                markerBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        markerCanvas.drawBitmap(markerBitmap, rect, rect, paint);
        markerBitmap.recycle();
        return output;
    }


    @Override
    public void onPhotoPagerReady() {
//        photoPagerAdapter.setFragmentImage(0, R.drawable.rockerfeller1);
//        photoPagerAdapter.setFragmentImage(1, R.drawable.rockerfeller2);
//        photoPagerAdapter.setFragmentImage(2, R.drawable.rockerfeller3);
//        photoPagerAdapter.setFragmentImage(3, R.drawable.rockerfeller4);
    }

    private void dropPinAnimation(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 10);
                }
            }
        });
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
        //This MUST be done before saving any of your own or your base class's variables
        final Bundle mapViewSaveState = new Bundle(outState);
        mapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        //Add any other variables here.
        outState.putBoolean("markersDisplayed", markersDisplayed);
        super.onSaveInstanceState(outState);
    }

    private class MapPanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            panelToolbar.setAlpha(1 - slideOffset);
        }

        @Override
        public void onPanelCollapsed(View panel) {

        }

        @Override
        public void onPanelExpanded(View panel) {

        }

        @Override
        public void onPanelAnchored(View panel) {

        }

        @Override
        public void onPanelHidden(View panel) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
//        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }
}


