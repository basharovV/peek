package com.peekapps.peek;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.peekapps.peek.activities.PlaceProfile;
import com.peekapps.peek.adapters.PlaceInfoWindowAdapter;
import com.peekapps.peek.database.PlaceDbHelper;
import com.peekapps.peek.place_api.Place;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Slav on 01/06/2015.
 */
public class DisplayMarkersTask implements Runnable{

    private Context context;
    private GoogleMap googleMap;
    private HashMap<Marker, Place> markerMap;
    private List<HashMap<String, String>> placesList;

    public DisplayMarkersTask(final Context context, GoogleMap map) {
        this.context = context;
        googleMap = map;

        markerMap = new HashMap<Marker, Place>();
    }

    @Override
    public void run() {
        googleMap.clear();
        //Fetch all place data from database and populate map with markers
        PlaceDbHelper dbHelper = new PlaceDbHelper(context);
        for (Place place : dbHelper.getAllPlaces()) {
            putMarker(place);
        }

        //Initialise the adapter for displaying a marker info window.
        //Set marker map for associating marker objects to place objects.
        PlaceInfoWindowAdapter infoWindowAdapter = new PlaceInfoWindowAdapter(context);
        infoWindowAdapter.setMarkerMap(markerMap);
        googleMap.setInfoWindowAdapter(infoWindowAdapter);

        //When clicked, display the correct place profile.
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Place pl = markerMap.get(marker);
                Intent profileIntent = new Intent(context, PlaceProfile.class);
                profileIntent.putExtra("place_object", pl);
                context.startActivity(profileIntent);
            }
        });
    }

    public void putMarker(Place pl) {
        ((FragmentActivity) context).runOnUiThread(new putMarkerRunnable(pl));
    }

    private class putMarkerRunnable implements Runnable {
        private Place pl;

        public putMarkerRunnable(Place pl) {
            this.pl = pl;
        }
        @Override
        public void run() {
            MarkerOptions markerOptions = new MarkerOptions();
            double latitude = pl.getLatitude();
            double longitude = pl.getLongitude();
            String placeName = pl.getName();
            String vicinity = pl.getVicinity();
            LatLng latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
            Marker m = googleMap.addMarker(markerOptions);

            markerMap.put(m, pl);
            dropPinAnimation(m);
        }
    }
    public List<HashMap<String, String>> getPlacesList() {
        return placesList;
    }

    private void dropPinAnimation(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                } else {

                }
            }
        });
    }

}
