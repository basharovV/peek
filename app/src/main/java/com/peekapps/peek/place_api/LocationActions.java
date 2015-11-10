package com.peekapps.peek.place_api;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by Slav on 16/08/2015.
 */
public class LocationActions {

    private static LocationActions instance = null;
    private LocationListener locationListener;

    private LocationActions() {}

    public LocationActions getInstance() {
        if (instance == null) {
            return new LocationActions();
        }
        return instance;
    }

    public void addListener(LocationListener listener) {
        locationListener = listener;
    }

    public void requestLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        Criteria placeCriteria = new Criteria();
        String networkProvider = LocationManager.NETWORK_PROVIDER;
        try {
            Location location = locationManager.getLastKnownLocation(networkProvider);
            if (location == null) {
                locationManager.requestLocationUpdates(networkProvider, 0, 0, locationListener);
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

    }


}
