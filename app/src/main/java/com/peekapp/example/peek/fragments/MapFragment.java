
package com.peekapp.example.peek.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.peekapp.example.peek.DownloadImageTask;
import com.peekapp.example.peek.place_api.Place;
import com.peekapp.example.peek.place_api.PlacesListener;
import com.peekapp.example.peek.R;

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
    double latitude = 36.5167; //Marbella coordinates
    double longitude = -4.8833;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getActivity().
                getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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
        CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        CameraPosition camPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                .zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));

    }
}

