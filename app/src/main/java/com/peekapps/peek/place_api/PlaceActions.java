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

import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
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

    private int RADIUS = 10000;
    double latitude = 40.7577; //NYC coordinates
    double longitude = -73.9857;
    private Location currentLocation;

    private static PlaceActions instance = null;

    protected PlaceActions() {
//        currentLocation = new Location("fakeNYC");
//        currentLocation.setLatitude(latitude);
//        currentLocation.setLongitude(longitude);
    }

    public static PlaceActions getInstance() {
        if (instance == null) {
            return new PlaceActions();
        }
        return instance;
    }

    public void setMockLocation(Context context) {
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
        String gpsProvider = LocationManager.GPS_PROVIDER;
        locationManager.addTestProvider(gpsProvider, false, false, false, false, false, true, true, 0, 5);
        locationManager.setTestProviderEnabled(gpsProvider, true);

        //Set mock location to Times Square, New York
        Location mockLocation = new Location(gpsProvider);
        mockLocation.setLatitude(latitude);
        mockLocation.setLongitude(longitude);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAltitude(0);
        try {
            Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
            if (locationJellyBeanFixMethod != null) {
                locationJellyBeanFixMethod.invoke(mockLocation);
            }
        }
        catch (Exception e) {}
        locationManager.setTestProviderLocation(gpsProvider, mockLocation);
        currentLocation = mockLocation;
    }

    /**
     * Get the distance from the user's location to a particular place
     * @param context
     * @param pl The place to calculate the distance to
     * @return The distance calculated in meters
     */
    public int distanceToPlace(Context context, Place pl) {
        Location myLocation = PlaceActions.getInstance().getLocation(context);
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

    public Place getPlace(String placeID, Context context) {
        PlacesRequestTask reqTask = new PlacesRequestTask();
        String url = generateUrl(placeID);
        Place pl = reqTask.fetchSinglePlace(url);
        if (pl != null) {
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
        demoPlaces.add(getPlace("ChIJmQJIxlVYwokRLgeuocVOGVU", context));
        demoPlaces.add(getPlace("ChIJPTacEpBQwokRKwIlDXelxkA", context));
        demoPlaces.add(getPlace("ChIJR_bK295bwokR8gM6QgEdmkY", context));
        updateDatabase(demoPlaces, context);
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
        placesList.add(getPlace("ChIJmQJIxlVYwokRLgeuocVOGVU", context));
        placesList.add(getPlace("ChIJPTacEpBQwokRKwIlDXelxkA", context));
        placesList.add(getPlace("ChIJR_bK295bwokR8gM6QgEdmkY", context));

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

        updateDatabase(placesList, context);

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

    private void updateDatabase(List<Place> placesList, Context context) {
        PlaceDbHelper dbHelper = new PlaceDbHelper(context);
        for (Place place : placesList) {
            dbHelper.addPlace(place);
        }
    }
}
