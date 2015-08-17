package com.peekapp.example.peek.place_api;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;

import com.peekapp.example.peek.database.PlaceDbHelper;

import java.util.HashMap;
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

