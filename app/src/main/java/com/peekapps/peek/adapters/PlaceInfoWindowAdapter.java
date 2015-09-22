package com.peekapps.peek.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.peekapps.peek.R;
import com.peekapps.peek.place_api.Place;

import java.util.HashMap;

/**
 * Created by Slav on 16/06/2015.
 */
public class PlaceInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View infoWindowView;
    private HashMap<Marker, Place> markerMap;

    public PlaceInfoWindowAdapter(Context context) {
        infoWindowView = ((FragmentActivity) context).getLayoutInflater().
                inflate(R.layout.view_map_info_window, null);

    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void setMarkerMap(HashMap<Marker, Place> markerMap) {
        this.markerMap = markerMap;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        //Find UI elements
        TextView infoTitle = (TextView) infoWindowView.findViewById(R.id.map_info_name);
        TextView infoType = (TextView) infoWindowView.findViewById(R.id.map_info_type);

        //Get place object associated to clicked marker.
        Place pl = markerMap.get(marker);

        //Set name of selected place
        infoTitle.setText(marker.getTitle());
        infoType.setText(pl.getType());

        //Pass the ImageView to async task to fetch and set the image in the background.
//        new DownloadImageTask(infoImg, null).execute(pl.getImageURL());

        return infoWindowView;
    }
}
