/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.peekapps.peek.data.location.observables.LocationUpdatesObservable;

import rx.Observable;

/**
 * Created by Slav on 03/03/2016.
 */
public class UserLocationManager {

    public static Observable<Location> getLastLocation(Context context) {
//        if ( Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(context,
//                        android.Manifest.permission.
//                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(context,
//                        android.Manifest.permission.
//                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            return null;
//        }
//        LocationManager locationManager = (LocationManager) context.
//                getSystemService(Context.LOCATION_SERVICE);
//        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (currentLocation == null) {
//            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }
//        return currentLocation;
        return Observable.empty();
    }

    public static Observable<Location> getUpdatedLocation(Context context) {
        return Observable.empty();
    }

    public static Observable<Location> requestLocationUpdates(Context context,
                                                              LocationRequest locationRequest) {
        return LocationUpdatesObservable.createObservable(context, locationRequest);
    }
}
