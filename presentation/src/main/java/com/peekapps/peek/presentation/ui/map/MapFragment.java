
/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.map;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.joooonho.SelectableRoundedImageView;
import com.peekapps.peek.data.media.BitmapUtils;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.DaggerFragmentComponent;
import com.peekapps.peek.presentation.common.di.components.FragmentComponent;
import com.peekapps.peek.presentation.common.di.components.MapComponent;
import com.peekapps.peek.presentation.common.di.modules.FragmentModule;
import com.peekapps.peek.presentation.common.navigation.Navigator;
import com.peekapps.peek.presentation.model.PlaceModel;
import com.peekapps.peek.presentation.ui.BaseFragment;
import com.peekapps.peek.presentation.ui.media.PhotoPager;
import com.peekapps.peek.presentation.ui.media.PhotoPagerAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends BaseFragment implements MapPlacesView{

    int[] uniIcons = {
            R.drawable.anglia_ruskin_university_480,
            R.drawable.aston_university_480,
            R.drawable.birkbeck__university_of_london_480,
            R.drawable.bournemouth_university_480,
            R.drawable.cranfield_university_480,
            R.drawable.de_montfort_university_480,
            R.drawable.dundee_university_480,
            R.drawable.durham_university_480
                    };

    //------Views
    //Sliding panel layout

    @Bind(R.id.mapFragment) SlidingUpPanelLayout mapSlidingPanel;

    //Panel elements
    @Bind(R.id.mapPanelToolbar)         Toolbar panelToolbar;
    @Bind(R.id.mapPhotoPager)           PhotoPager photoPager;
    @Bind(R.id.mapPanelPlaceName)       TextView panelHeaderName;
    @Bind(R.id.mapPanelPlaceVic)        TextView panelHeaderVic;
    @Bind(R.id.mapPanelPlaceIcon)
    SelectableRoundedImageView panelHeaderIcon;
    @Bind(R.id.mapLikeButton)           LinearLayout likeButton;
    @Bind(R.id.mapPanelUploadCountIcon) ImageView uploadCountIcon;
    @Bind(R.id.mapView)                 MapView mapView;

    @Inject
    PhotoPagerAdapter photoPagerAdapter;

    @Inject
    Navigator navigator;

    private GoogleMap googleMap;
    private boolean markersDisplayed;

    @Inject
    MapPresenter mapPresenter;

    FragmentComponent fragmentComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        if ( mapView != null ) {
            mapView.onCreate(mapViewSavedInstanceState);
        }
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
        ButterKnife.bind(this, rootView);
        initializeComponents();
        initializeFragment();
//        mapView.getMapAsync((OnMapReadyCallback) getActivity());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapPresenter.setMapView(this);
        mapPresenter.initialize();
    }

    private void initializeComponents() {
//        this.getComponent(MapComponent.class).inject(this);
        fragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .activityModule(getActivityModule())
                .build();
        fragmentComponent.inject(this);
    }

    public void setPlacesList() {
//        PlaceDbHelper dbHelper = new PlaceDbHelper(getActivity());
//        placesList = dbHelper.getAllPlaces();
    }

    public boolean hasMarkers() {
        return markersDisplayed;
    }



    private void initializeFragment() {
        initializeMap();
        initializePlacePanel();
    }

    private void initializeMap() {
        mapView.onCreate(null);
        mapView.onResume();
        //-----------GOOGLE MAP ---------
        googleMap = mapView.getMap();
        googleMap.setOnMarkerClickListener(new OnPlaceSelectedListener());
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapSlidingPanel.isEnabled()) {
                    mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });
    }

    private void initializePlacePanel() {
        //Init
        mapSlidingPanel.setPanelSlideListener(new MapPanelSlideListener());
        mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mapSlidingPanel.setOverlayed(true);

        //Main photo pager
        photoPagerAdapter = new PhotoPagerAdapter(getChildFragmentManager());
        photoPager.setAdapter(photoPagerAdapter);

        //Header elements
        uploadCountIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.peek_dark_grey));
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // --------- User interaction ------------

    @OnClick(R.id.mapPanelPlaceName)
    public void goToUniProfile() {
        navigator.navigateToUniProfile(getContext(), 0);
    }

    public class OnPlaceSelectedListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            mapPresenter.onMarkerClick(marker);

            return true;
        }
    }

    public void enableLocation() {
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.d("MapFragment", "Setting location error: no permission");
        }

//        currentLocation = PlaceActions.getInstance(getActivity()).getLocation(getActivity());
//        if (currentLocation != null) {
//            //ANIMATE MAP
//            //--------GOOGLE MAP----------
//            CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(),
//                    currentLocation.getLongitude()))
//                    .zoom(5).build();
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    // ------------------------- Data -------------------------

    public void renderPlaceDetails(University placeModel) {
        panelHeaderName.setText(placeModel.getName());
        panelHeaderVic.setText(placeModel.getCity());

        // Temporary hack
        int imageId = getResources().getIdentifier(placeModel.getId(), "drawable", getContext().getPackageName());
        panelHeaderIcon.setImageBitmap(BitmapUtils.generateCircleMarkerBitmap(
                BitmapFactory.decodeResource(getResources(), imageId), false));
        // likes
        // number of photos
    }

    @Override
    public void displayMarker(final University place) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MarkerOptions markerOptions = MapUtils.getMarkerOptions(getContext(), place);
                Marker marker = googleMap.addMarker(markerOptions);
                dropPinAnimation(marker);
                mapPresenter.addMarker(marker, place);
            }
        });
    }

    @Override
    public void animateTo(double latitude, double longitude) {

    }

    public void updateLocation(Location location) {
        //GOOGLE MAP
        CameraPosition camPosition = new CameraPosition.Builder().target
                (new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

    }
    // -------------------------------------------------
    @Override
    public void showToastMessage(String message) {
        super.showToastMessage(message);
    }

    public void onPermissionsGranted() {
        enableLocation();
    }

    private class PutMarkerRunnable implements Runnable {
        private MarkerOptions markerOptions;
        private University place;

        public PutMarkerRunnable(University place) {
            this.place = place;
            this.markerOptions = MapUtils.getMarkerOptions(getContext(), place);
        }

        @Override
        public void run() {
            Marker marker = googleMap.addMarker(markerOptions);
            dropPinAnimation(marker);
            mapPresenter.addMarker(marker, place);
        }
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
    //rename to media panel
    @Override
    public void collapsePlacePanel() {
        if (mapSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            mapSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
        mapPresenter.destroy();
//        mapView.onDestroy();
    }
}


