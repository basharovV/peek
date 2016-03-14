/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

/**
 * Created by Slav on 26/05/2015.
 */
public class PlaceModel implements Serializable {

    private final String placeId;
    private String name;
    private String type;
    private String vicinity;
    private String imageURL;
    private double latitude;
    private double longitude;
    private int timeUpdated;

    private int numberOfPhotos;
    private int minutesAgoUpdated;
    private int distance;

    private int imageResource;

    public PlaceModel(String id) {
        this.placeId= id;
    }

    public PlaceModel(String id, String name, String vicinity, String type, String img) {
        this.placeId = id;
        this.name = name;
        this.vicinity = vicinity;
        this.type = type;
        this.imageURL = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setImageResource(int resource) {
        this.imageResource = resource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public Bitmap getImageBitmap() {
        return Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
    }

    public String getID() {
        return placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(int timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public int getMinutesAgoUpdated() {
        return minutesAgoUpdated;
    }

    public void setMinutesAgoUpdated(int minutes) {
        this.minutesAgoUpdated = minutes;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }
}

