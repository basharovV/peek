/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.peekapps.peek.data.media.BitmapUtils;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.model.PlaceModel;

/**
 * Created by Slav on 03/03/2016.
 */
public class MapUtils {

    public static MarkerOptions getMarkerOptions(Context context, University pl) {
        MarkerOptions markerOptions = new MarkerOptions();
        double latitude = pl.getLatitude();
        double longitude = pl.getLongitude();
        String placeName = pl.getName();
//        String vicinity = pl.getVicinity();
        LatLng latLng = new LatLng(latitude, longitude);
        markerOptions.position(latLng);
        markerOptions.title(placeName);

        int imageId = context.getResources().getIdentifier(pl.getId(), "drawable", context.getPackageName());
        if (imageId != 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.generateCircleMarkerBitmap(
                    BitmapFactory.decodeResource(context.getResources(), imageId), true)));
        }
        else {
            markerOptions.visible(false);
        }
        return markerOptions;
    }
}
