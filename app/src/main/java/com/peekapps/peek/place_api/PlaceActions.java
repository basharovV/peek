package com.peekapps.peek.place_api;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.peekapps.peek.PermissionActions;
import com.peekapps.peek.database.PlaceDbHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Slav on 25/07/2015.
 */
public class PlaceActions {

    private final static String API_KEY = "AIzaSyCmBci73i_WF97IB1IKYnIIkXR1_NeECBw";

    /**Types of requests to be parsed
     * NEARBY_PLACES_DETAILS will be used to parse multiple JSON arrays
     * SINGLE_PLACE_DETAILS will be used to parse details for one JSON array.
     */
    public static final int NEARBY_PLACES = 0;
    public static final int SINGLE_PLACE = 1;

    private Location currentLocation;

    private static PlaceActions instance = null;
    private Context context;

    protected PlaceActions(Context context) {
        this.context = context;
    }

    public static PlaceActions getInstance(Context context) {
        if (instance == null) {
            return new PlaceActions(context);
        }
        return instance;
    }

    public void requestLocationUpdates(GoogleApiClient googleApiClient,
                                       LocationListener locationListener) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (Build.VERSION.SDK_INT >= 23
            &&PermissionActions.getMissingPermissions(context).isEmpty()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, locationListener, null);
            }
            catch (SecurityException e) {
                Log.e("PlaceActions", "Permission not granted - location");
            }
        }
        else {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, locationListener, null);
            }
            catch (SecurityException e) {
                Log.e("PlaceActions", "Permission not granted - location");
            }
        }
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
    public int distanceToPlace(Context context, Place pl) {
        Location myLocation = getLocation(context);
        Location placeLocation = new Location("place");
        placeLocation.setLatitude(pl.getLatitude());
        placeLocation.setLongitude(pl.getLongitude());
        float dist = myLocation.distanceTo(placeLocation);
        return Math.round(dist);
    }

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

    public Place getPlace(Context context, String placeID, boolean savePhoto) {
        PlacesRequestTask reqTask = new PlacesRequestTask();
        String url = generateUrl(placeID);
        Place pl = reqTask.fetchSinglePlace(url);
        if (pl != null && savePhoto) {
            if (pl.hasPhoto()) {
                GooglePhotoFetchTask photoFetchTask = new GooglePhotoFetchTask();
                Object[] toPass = new Object[3];
                toPass[0] = context;
                toPass[1] = pl;
                photoFetchTask.execute(toPass);
            }
        }
        return pl;
    }

    public void addDemoPlaces(Context context) {
        List<Place> demoPlaces = new ArrayList<>();
        demoPlaces.add(getPlace(context, "ChIJmQJIxlVYwokRLgeuocVOGVU", true));
        demoPlaces.add(getPlace(context, "ChIJPTacEpBQwokRKwIlDXelxkA", true));
        demoPlaces.add(getPlace(context, "ChIJR_bK295bwokR8gM6QgEdmkY", true));
        updateDatabase(context, demoPlaces);
    }

    public void setCurrentLocation(Location location) {
        currentLocation = location;
    }

    public Location getLocation(Context context) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.
                                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.
                                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return null;
        }
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation == null) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return currentLocation;
    }

    public List<Place> getNearbyPlaces(Context context) {
        currentLocation = getLocation(context);
        PlacesRequestTask reqTask = new PlacesRequestTask();
        String url = generateUrl(null);
        List<Place> placesList = reqTask.fetchNearbyPlaces(url);
        if (placesList == null) {
            return null;
        }
        //Fpr Demo video purposes
        placesList.add(getPlace(context, "ChIJmQJIxlVYwokRLgeuocVOGVU", true));
        placesList.add(getPlace(context, "ChIJPTacEpBQwokRKwIlDXelxkA", true));
        placesList.add(getPlace(context, "ChIJR_bK295bwokR8gM6QgEdmkY", true));

        GooglePhotoFetchTask photoTask = new GooglePhotoFetchTask();
        Object[] toPass = new Object[3];
        toPass[0] = context;
        toPass[2] = placesList;
        photoTask.execute(toPass);

        for (Place pl : placesList) {
            //Create a Photo Request for the place, store in cache
            /* Retrieve the 'last updated' time attribute (eg. 9 min ago)
             * Set and format the time of upload -RANDOMISED FOR TEST VERSION
             */
            Random random = new Random();
            int randomTime = random.nextInt(31);
            pl.setMinutesAgoUpdated(randomTime);
            //Calculate and set the distance to the place
            int distance = distanceToPlace(context, pl);
            pl.setDistance(distance);

            int randomPopularity = random.nextInt(placesList.size());
            pl.setNumberOfPhotos(randomPopularity);
        }

        updateDatabase(context, placesList);

        return placesList;
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

    public static void updateDatabase(Context context, List<Place> placesList) {
        PlaceDbHelper dbHelper = new PlaceDbHelper(context);
        for (Place place : placesList) {
            dbHelper.addPlace(place);
        }
    }
}
