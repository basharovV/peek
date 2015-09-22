package com.peekapps.peek.place_api;

import java.io.Serializable;

/**
 * Created by Slav on 19/07/2015.
 */

public class PlacePhoto implements Serializable{
    private String photoRef;
    private double maxWidth;
    private double maxHeight;

    public PlacePhoto(String ref) {
        photoRef = ref;
        maxWidth = 500;
        maxHeight = 500;
    }
    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }
}
