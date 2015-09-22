package com.peekapps.peek.place_api;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import java.util.List;

/**
 * Created by Slav on 06/06/2015.
 */
public class PlacesTask extends AsyncTask<Void, Void, Void> {

    //Google Places API Key


    private List<Place> placesList;
    private PlacesListener placesListener;

    @Override
    protected Void doInBackground(Void... params) {
        placesList = PlaceActions.getInstance().getNearbyPlaces((FragmentActivity) placesListener);
        if (placesList != null) {
            notifyListener();
        }
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

