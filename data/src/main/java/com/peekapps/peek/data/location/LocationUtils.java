/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

/**
 * Created by Slav on 06/05/2016.
 */
public class LocationUtils {

    public static LatLngBounds suggestedUniBounds(LatLng centerPoint, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(centerPoint, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(centerPoint, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
}
