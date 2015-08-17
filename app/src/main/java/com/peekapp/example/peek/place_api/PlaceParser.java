package com.peekapp.example.peek.place_api;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peekapp.example.peek.database.PlaceDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Slav on 01/06/2015.
 */
public class PlaceParser {

    private List<Place> placesList;


    public PlaceParser() {

    }

    public List<Place> parseNearby(JSONObject jsonObject) {
        //Convert JSON object to array for parsing/iterating
        JSONArray jsonArray = null;
        try {
          jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlacesList(jsonArray);
    }

    public Place parseSingle(JSONObject jsonObject) {
        //Convert JSON object to array for parsing/iterating
        JSONObject jsonPlace= null;
        try {
            jsonPlace = jsonObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlace(jsonPlace);
    }

    public List<Place> getPlacesList(JSONArray jsonArray) {
        placesList = new ArrayList<Place>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Place pl = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(pl);
            } catch (JSONException e) {
                Log.d("JSONException", e.toString());
            }
        }
        return placesList;
    }

    public Place getPlace(JSONObject placeObj) {
        Place pl = new Place();

        try {
            pl.setID(placeObj.getString("place_id"));
            if (!placeObj.isNull("name")) {
                pl.setName(placeObj.getString("name"));
            }
            if (!placeObj.isNull("vicinity")) {
                pl.setVicinity(placeObj.getString("vicinity"));
            }
            pl.setLatitude(Double.parseDouble(placeObj.getJSONObject("geometry").
                    getJSONObject("location").getString("lat")));
            pl.setLongitude(Double.parseDouble(placeObj.getJSONObject("geometry").
                    getJSONObject("location").getString("lng")));
            pl.setType(placeObj.getJSONArray("types").getString(0));
            pl.setImageURL(placeObj.getString("icon"));
            pl.setPhoto(new PlacePhoto(placeObj.getJSONArray("photos").
                    getJSONObject(0).getString("photo_reference")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pl;
    }

}
