package com.peekapp.example.peek.place_api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.places.Places;
import com.peekapp.example.peek.database.PlaceDbHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
//    double latitude = 36.5167; //Marbella coordinates
//    double longitude = -4.8833;
    private Location currentLocation;

    private static PlaceActions instance = null;

    protected PlaceActions() {}

    public static PlaceActions getInstance() {
        if (instance == null) {
            return new PlaceActions();
        }
        return instance;
    }

    public Place getPlace(String placeID, Context context) {
        PlacesRequestTask reqTask = new PlacesRequestTask();
        String url = generateUrl(placeID);
        Place pl = reqTask.fetchSinglePlace(url);
        if (pl.hasPhoto()) {
            downloadPhoto(pl, context);
        }
        return pl;
    }

    public List<Place> getNearbyPlaces(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        PlacesRequestTask reqTask = new PlacesRequestTask();
        String url = generateUrl(null);
        List<Place> placesList = reqTask.fetchNearbyPlaces(url);
        updateDatabase(placesList, context);
        for (Place pl : placesList) {
            if (pl.hasPhoto()) {
                downloadPhoto(pl, context);
            }
        }
        return placesList;
        }

    public String generateUrl(String placeID) {
        StringBuilder placesUrl = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/");
        if (placeID == null) {
            placesUrl.append("nearbysearch/json?");
            placesUrl.append("location=" + currentLocation.getLatitude()
                    + "," + currentLocation.getLongitude());
            placesUrl.append("&radius=" + RADIUS);
            placesUrl.append("&types=bar|night_club");
            placesUrl.append("&keyword=bar");
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
