
package com.peekapps.peek.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.place_api.PlacesListener;
import com.peekapps.peek.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapFragment extends Fragment implements OnPermissionsListener {

    //Implement a observer/listeners
    private CopyOnWriteArrayList<PlacesListener> placesListeners;


    //------Views
    //Sliding panel layout
    private SlidingUpPanelLayout mapSlidingPanel;
    private MapView mapView;
    private GoogleMap googleMap;

    private Location currentLocation;


    private int RADIUS = 6000;
    double latitude = 40.7127; //NYC coordinates
    double longitude = -74.0059;
//    double latitude = 36.5167; //Marbella coordinates
//    double longitude = -4.8833;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check for permission
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.
                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.
                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
    savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        //Find views
        mapSlidingPanel = (SlidingUpPanelLayout) rootView.findViewById(R.id.mapFragment);
        mapView = (MapView) rootView.findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync((OnMapReadyCallback) getActivity());

        setupMap();
        setupSlidingPanel();

        if (Build.VERSION.SDK_INT >= 23) {
            if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
                enableLocation();
            }
        }
        else enableLocation();

        return rootView;
    }

    private void setupMap() {
        googleMap = mapView.getMap();
    }

    private void setupSlidingPanel() {
        //Init
        mapSlidingPanel.setEnabled(false);
    }

    public class OnPlaceSelectedListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    }

    public void enableLocation() {
        currentLocation = PlaceActions.getInstance().getLocation(getActivity());
        CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude()))
                .zoom(5).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    public void updateLocation(Location location) {
        currentLocation = location;
        CameraPosition camPosition = new CameraPosition.Builder().target
                (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    @Override
    public void onPermissionsGranted() {
        enableLocation();
    }
}


