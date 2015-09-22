
package com.peekapps.peek.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.place_api.PlacesListener;
import com.peekapps.peek.R;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapFragment extends Fragment{

    //Implement a observer/listeners
    private CopyOnWriteArrayList<PlacesListener> placesListeners;

    private MapView mapView;
    private GoogleMap googleMap;
    private Toolbar toolbar;
    private SearchView searchView;
    private List<HashMap<String, String>> placesList;

    private int RADIUS = 6000;
    double latitude = 40.7127; //NYC coordinates
    double longitude = -74.0059;
    private Location currentLocation;
//    double latitude = 36.5167; //Marbella coordinates
//    double longitude = -4.8833;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocation = PlaceActions.getInstance().getLocation(getActivity());
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

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync((OnMapReadyCallback) getActivity());
        googleMap = mapView.getMap();
        currentLocation = PlaceActions.getInstance().getLocation(getActivity());
        CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude()))
                .zoom(1).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

        //Set up listeners
        this.placesListeners = new CopyOnWriteArrayList<PlacesListener>();

        return rootView;
    }

    public List<HashMap<String, String>> getPlacesList() {
        return placesList;

    }

    public void updateLocation(Location location) {
        currentLocation = location;
        CameraPosition camPosition = new CameraPosition.Builder().target
                (new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

    }
}

