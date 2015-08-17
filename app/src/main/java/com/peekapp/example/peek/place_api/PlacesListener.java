package com.peekapp.example.peek.place_api;

import android.location.Location;

/**
 * Created by Slav on 02/06/2015.
 */
public interface PlacesListener {
    public void onPlacesFetched(PlacesFetchedEvent e);
}
