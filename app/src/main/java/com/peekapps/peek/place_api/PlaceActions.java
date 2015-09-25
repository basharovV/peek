package com.peekapps.peek.place_api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

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

    public String distanceToPlace(Context context, Place pl) {
        Location myLocation = PlaceActions.getInstance().getLocation(context);
        Location placeLocation = new Location("place");
        placeLocation.setLatitude(pl.getLatitude());
        placeLocation.setLongitude(pl.getLongitude());
        float dist = myLocation.distanceTo(placeLocation);
        if (dist > 1000) {
            dist = dist / 1000;
            return String.format("%.2f", dist) + " km";
        }
        return (String.format("%.2f", dist)) + " m";
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
                downloadPhoto(pl, context);
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
        for (Place pl : placesList) {
            if (pl.hasPhoto()) {
                downloadPhoto(pl, context);
            }
        }
        //Fpr Demo video purposes
        placesList.add(getPlace("ChIJmQJIxlVYwokRLgeuocVOGVU", context));
        placesList.add(getPlace("ChIJPTacEpBQwokRKwIlDXelxkA", context));
        placesList.add(getPlace("ChIJR_bK295bwokR8gM6QgEdmkY", context));
        updateDatabase(placesList, context);

        return placesList;
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


    private void downloadPhoto(Place pl, Context context) {

        try {
            StringBuilder urlBuilder = new StringBuilder
                    ("https://maps.googleapis.com/maps/api/place/photo?");
            urlBuilder.append("maxwidth=3000");
            urlBuilder.append("&maxheight=3000");
            urlBuilder.append("&photoreference=" + pl.getPhoto().getPhotoRef());
            urlBuilder.append("&key=" + API_KEY);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //Download bitmap
            Bitmap photo = BitmapFactory.decodeStream(inputStream);
            savePhoto(photo, pl.getID(), context);
            reader.close();
            inputStream.close();
            urlConnection.disconnect();
        }
        catch (MalformedURLException e) {
            Log.d("PhotoReq", "Malformed URL");
        }
        catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        }
    }

    private void savePhoto(Bitmap bmp, String id, Context context) {
        try {
            File photoFile = new File(context.getExternalCacheDir() + "/" + id + "photo.jpg");
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        }
    }


}
