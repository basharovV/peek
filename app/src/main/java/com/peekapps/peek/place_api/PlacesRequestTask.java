package com.peekapps.peek.place_api;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Task to request results on places using the Google Places API. The resulting URL can then be
 * used to display place details .
 * @author Vyacheslav Basharov
 * @version 01/06/2015.
 */
public class PlacesRequestTask {
    /**
     * placesData : Stores JSON string containing the places results
     * placesJSON : Stores the JSON Object to be parsed into a list with place details
     * placesList : Stores parsed JSON Object as a list of hashmaps for each place.
     */
    private String placesData = null;
    private JSONObject placesJSON = null;

    //Depending on what is being fetched, a list or Place object will be stored.
    private List<Place> placesList;
    private Place pl;

    private PlacesListener requestListener;
    private PlaceParser placeParser;

    private static final String TAG = "PlacesReqTask";

    public PlacesRequestTask() {
        placeParser = new PlaceParser();
    }

    public List<Place> fetchNearbyPlaces(String url) {
        try {
            placesData = downloadUrl(url);
            placesJSON = new JSONObject(placesData);
            placesList = placeParser.parseNearby(placesJSON);

        }
        catch (IOException e) {
            Log.d("Error in background", e.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "JSON Exception, error fetching data");
            e.printStackTrace();
        }
        return placesList;
    }

    public Place fetchSinglePlace(String url) {
        try {
            placesData = downloadUrl(url);
            placesJSON = new JSONObject(placesData);
            pl = placeParser.parseSingle(placesJSON);

        }
        catch (IOException e) {
            Log.d("Error in background", e.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "JSON Exception, error fetching data");
        }
        return pl;
    }

    private String downloadUrl(String urlString) throws IOException{
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            data = buffer.toString();
            reader.close();
            inputStream.close();
            urlConnection.disconnect();
        }
        catch (NullPointerException e) {
            Log.d("NPE in buffer/stream", e.toString());
        }
        catch (Exception e) {
            Log.d("Exception fetching url ", e.toString());
        }

        return data;
    }

    public List<Place> getPlacesList() {
        return placesList;
    }
    public Place getPlace() { return pl;}

    public void attachListener(PlacesListener listener) {
        requestListener = listener;
    }

    private void notifyListener() {
        PlacesFetchedEvent event = new PlacesFetchedEvent(this);
        requestListener.onPlacesFetched(event);
    }
}
