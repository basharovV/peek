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
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.peekapps.peek.data.entity.mapper.UserLocationMapper;
import com.peekapps.peek.data.location.observables.LastKnownLocationObservable;
import com.peekapps.peek.data.location.observables.LocationUpdatesObservable;
import com.peekapps.peek.domain.UserLocation;
import com.peekapps.peek.domain.services.LocationProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Slav on 25/07/2015.
 */

@Singleton
public class LocationProviderImpl implements LocationProvider {

    private final static String API_KEY = "AIzaSyCmBci73i_WF97IB1IKYnIIkXR1_NeECBw";

    /**Types of requests to be parsed
     * NEARBY_PLACES_DETAILS will be used to parse multiple JSON arrays
     * SINGLE_PLACE_DETAILS will be used to parse details for one JSON array.
     */
    public static final int NEARBY_PLACES = 0;
    public static final int SINGLE_PLACE = 1;

    private Location currentLocation;
    private UserLocationMapper userLocationMapper;
    private Context context;

    @Inject
    public LocationProviderImpl(Context context, UserLocationMapper userLocationMapper) {
        this.userLocationMapper = userLocationMapper;
        this.context = context;
    }

    @Override
    public Observable<UserLocation> getLastLocation() {
            return LastKnownLocationObservable.createObservable(context)
                    .map(new Func1<Location, UserLocation>() {
                        @Override
                        public UserLocation call(Location location) {
                            return userLocationMapper.transform(location);
                        }
                    });
    }

    @Override
    public Observable<UserLocation> getLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return LocationUpdatesObservable.createObservable(context, locationRequest)
                .map(new Func1<Location, UserLocation>() {
                    @Override
                    public UserLocation call(Location location) {
                        return userLocationMapper.transform(location);
                    }
                })
                .doOnNext(new Action1<UserLocation>() {
                    @Override
                    public void call(UserLocation userLocation) {
                        userLocation.setCountry("Sample Country");
                    }
                });

    }

    private Observable<UserLocation> getAdditionalLocationObservable(final UserLocation location) {
        return Observable.create(new Observable.OnSubscribe<UserLocation>() {
            @Override
            public void call(Subscriber<? super UserLocation> subscriber) {
                location.setCountry("Sample Country");
            }
        });
    }

    public void clearMockLocation(Context context) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.
                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.
                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
        }
        catch (IllegalArgumentException e) {
            Log.e("PlaceActions", "Can't delete GPS data");
        }
        try {
            locationManager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
        catch (IllegalArgumentException e) {
            Log.e("PlaceActions", "No such  provider");
        }

    }
//    public void setMockLocation(Context context) {
//        if ( Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(context,
//                        android.Manifest.permission.
//                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(context,
//                        android.Manifest.permission.
//                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//            return;
//        }
//        LocationManager locationManager = (LocationManager) context.
//                getSystemService(Context.LOCATION_SERVICE);
//        String gpsProvider = LocationManager.GPS_PROVIDER;
//        locationManager.addTestProvider(gpsProvider, false, false, false, false, false, true, true, 0, 5);
//        locationManager.setTestProviderEnabled(gpsProvider, true);
//
//        //Set mock location to Times Square, New York
//        Location mockLocation = new Location(gpsProvider);
//        mockLocation.setLatitude(latitude);
//        mockLocation.setLongitude(longitude);
//        mockLocation.setTime(System.currentTimeMillis());
//        mockLocation.setAltitude(0);
//        try {
//            Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
//            if (locationJellyBeanFixMethod != null) {
//                locationJellyBeanFixMethod.invoke(mockLocation);
//            }
//        }
//        catch (Exception e) {}
//        locationManager.setTestProviderLocation(gpsProvider, mockLocation);
//        currentLocation = mockLocation;
//    }

    /**
     * Get the distance from the user's location to a particular place
     * @param context
     * @param pl The place to calculate the distance to
     * @return The distance calculated in meters
     */
//    public int distanceToPlace(Context context, Place pl) {
//        Location myLocation = getLocation(context);
//        Location placeLocation = new Location("place");
//        placeLocation.setLatitude(pl.getLatitude());
//        placeLocation.setLongitude(pl.getLongitude());
//        float dist = myLocation.distanceTo(placeLocation);
//        return Math.round(dist);
//    }

    /**
     * Format the distance to be displayed in a TextField
     * @param dist The distance to the place in meters
     * @return A formatted String for the distance in meters or kilometers (miles)
     */
    public String formatDistance(int dist) {
        if (dist > 1000) {
            dist = dist / 1000;
            return dist + " km";
        }
        return dist + " m";
    }

    public Location getCurrentLocation(Context context) {
        return currentLocation;
    }

//    public Place getPlace(Context context, String placeID, boolean savePhoto) {
//        PlacesRequestTask reqTask = new PlacesRequestTask();
//        String url = generateUrl(placeID);
//        Place pl = reqTask.fetchSinglePlace(url);
//        if (pl != null && savePhoto) {
//            if (pl.hasPhoto()) {
//                GooglePhotoFetchTask photoFetchTask = new GooglePhotoFetchTask();
//                Object[] toPass = new Object[3];
//                toPass[0] = context;
//                toPass[1] = pl;
//                photoFetchTask.execute(toPass);
//            }
//        }
//        return pl;
//    }

    public void setCurrentLocation(Location location) {
        currentLocation = location;
    }


    public com.google.android.gms.location.places.Place getMostLikelyPlace(PlaceLikelihoodBuffer likelyPlaces) {
        float highestLikelihood = 0;
        com.google.android.gms.location.places.Place topPlace = null;
        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
            float currentLikelihood = placeLikelihood.getLikelihood();
            Log.i("PlaceLikelihood", String.format("Place '%s' has likelihood: %g",
                    placeLikelihood.getPlace().getName(),
                    placeLikelihood.getLikelihood()));
            if (highestLikelihood == 0) {
                highestLikelihood = placeLikelihood.getLikelihood();
                topPlace = placeLikelihood.getPlace();
            } else {
                if (currentLikelihood > highestLikelihood) {
                    highestLikelihood = currentLikelihood;
                    topPlace = placeLikelihood.getPlace();
                }
            }
        }
        return topPlace;
    }

    public String generateUrl(String placeID) {
        StringBuilder placesUrl = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/");
        if (placeID == null) {
            placesUrl.append("nearbysearch/json?");
            // Current location
            placesUrl.append("location=" + currentLocation.getLatitude()
                    + "," + currentLocation.getLongitude());
//            placesUrl.append("location=" + latitude
//                            + "," + longitude);
            placesUrl.append("&types=establishment");
            placesUrl.append("&radius=6000");
            placesUrl.append("&sensor=true");
            placesUrl.append("&key=" + API_KEY);
        } else {
            placesUrl.append("details/json?");
            placesUrl.append("placeid=" + placeID);
            placesUrl.append("&key=" + API_KEY);
        }
        return placesUrl.toString();
    }
//
//    public static void updateDatabase(Context context, List<Place> placesList) {
//        PlaceDbHelper dbHelper = new PlaceDbHelper(context);
//        for (Place place : placesList) {
//            dbHelper.addPlace(place);
//        }
//    }
}
