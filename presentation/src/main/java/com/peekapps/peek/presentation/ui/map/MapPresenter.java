/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.map;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.peekapps.peek.data.media.BitmapUtils;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.interactor.DefaultSubscriber;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.presentation.Presenter;
import com.peekapps.peek.presentation.model.PlaceModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Slav on 02/03/2016.
 */
public class MapPresenter implements Presenter {

    MapPlacesView mapView;

    // State constants
    private static final int PANEL_HIDDEN = 0;
    private static final int PANEL_COLLAPSED = 1;
    private static final int PANEL_EXPANDED = 2;

    // Current states
    private int panelState = PANEL_HIDDEN;

    private PlaceModel selectedPlace; // can be null
    private HashMap<Marker, University> markerMap;
    private List<PlaceModel> placesList;
    private Location currentLocation;

    private Interactor getUniversitiesInteractor;

    @Inject
    public MapPresenter(@Named("universities") Interactor getUniversitiesInteractor) {
        this.getUniversitiesInteractor = getUniversitiesInteractor;
        markerMap = new HashMap<>();
    }

    public void setMapView(@NonNull MapPlacesView mapView) {
        this.mapView = mapView;
    }

    public void initialize() {
//        showLoading();
        loadPlaces();
    }

    /*
        User interactions are handled below
     */

    public void onMarkerClick(Marker marker) {
        collapsePlacePanel();
        if (markerMap.containsKey(marker)) {
            University selectedPlace = markerMap.get(marker);
            if (selectedPlace != null) {
                showPlaceDetailsInView(selectedPlace);
            }
        }

//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    /*
        UI updates are handled in here
     */

    // Unrelated to data

    public void expandPlacePanel() {

    }

    public void collapsePlacePanel() {

    }

    public void hidePlacePanel() {

    }

    // Related to data

    private void displayMarker(University placeModel) {
        mapView.displayMarker(placeModel);
    }

    private void displayMarkers(List<University> universities) {
//        Log.d("MapPresenter", universities.toString());
        for (University uni : universities) {
            mapView.displayMarker(uni);
//            Log.d("MapPresenter", "displayed marker");
        }
    }
//    public void displayAllMarkers() {
//
//        markersDisplayed = true;
//        int count = 0;
//        for (PlaceModel pl : placesList) {
//            getActivity().runOnUiThread(new PutMarkerRunnable(pl, count));
//            count++;
//        }
//    }

    // ---------- Data -------------

    private void showPlaceDetailsInView(University placeModel) {
        mapView.renderPlaceDetails(placeModel);
    }

    private void loadPlaces() {
        getUniversitiesInteractor.execute(new OnUniversitiesReadySubscriber());
    }

    private class OnUniversitiesReadySubscriber extends DefaultSubscriber<List<University>> {
        @Override
        public void onCompleted() {
            Log.d("MapPresenter", "Univesities ready!");
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }

        @Override
        public void onNext(final List<University> universities) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    displayMarkers(universities);
                }
            }).start();
        }
    }

    // Other  - updates from Fragment

    public void addMarker(Marker marker, University place) {
        markerMap.put(marker, place);
    }



    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
