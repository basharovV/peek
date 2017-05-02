/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain;

/**
 * Created by Slav on 06/05/2016.
 */
public class LatLngBounds {

    private double southWestLat = 0;
    private double southWestLong = 0;
    private double northEastLat = 0;
    private double northEastLong = 0;

    public LatLngBounds(double southWestLat, double southWestLong, double northEastLat, double northEastLong) {
        this.southWestLat = southWestLat;
        this.southWestLong = southWestLong;
        this.northEastLat = northEastLat;
        this.northEastLong = northEastLong;
    }

    public double getSouthWestLat() {
        return southWestLat;
    }

    public double getSouthWestLong() {
        return southWestLong;
    }

    public double getNorthEastLat() {
        return northEastLat;
    }

    public double getNorthEastLong() {
        return northEastLong;
    }
}
