package com.peekapps.peek.place_api;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.peekapps.peek.backend_api.ParseUtils;

import java.util.List;

/**
 * Created by Slav on 06/06/2015.
 */
public class PlacesTask extends AsyncTask<Object, Void, Void> {

    //Google Places API Key


    private List<Place> placesList;
    private PlacesListener placesListener;

    @Override
    protected Void doInBackground(Object... params) {
        //With google
        placesList = PlaceActions.getInstance((Context) placesListener).getNearbyPlaces((FragmentActivity) placesListener);

        //Get Parse nearby places
//        ParseQuery<PlaceObject> placesQuery = ParseQuery.getQuery("Place");
//        ParseGeoPoint userGeoPoint = new ParseGeoPoint();
////        Location userLocation =  PlaceActions.getInstance().getCurrentLocation((Activity) placesListener);
//        Location userLocation = (Location) params[0];
//        userLocation.setLatitude(userLocation.getLatitude());
//        userLocation.setLongitude(userLocation.getLongitude());
//        placesQuery.whereNear("placeGeoPoint", userGeoPoint);
//        placesQuery.setLimit(10);
//        try {
//            List<PlaceObject> plObjects = placesQuery.find();
//            placesList = ParseUtils.toPeekPlaceArray((Activity) placesListener, plObjects);
//            PlaceActions.updateDatabase((Activity) placesListener, placesList);
//        }
//        catch (ParseException e) {
//            Log.e("PlaceFetchTask", e.toString());
//            }
        if (placesList != null) {
            notifyListener();
        }
        placesListener = null;
        placesList = null;
        return null;
    }

    public void attachListener(PlacesListener listener) {
        placesListener = listener;
    }

    private void notifyListener() {
        PlacesFetchedEvent event = new PlacesFetchedEvent(this);
        placesListener.onPlacesFetched(event);
    }

}

