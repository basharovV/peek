package com.peekapps.peek.place_api;

import java.io.Serializable;

/**
 * Created by Slav on 26/05/2015.
 */
public class Place implements Serializable {
    private String ID;
    private String name;
    private String type;
    private String vicinity;
    private String imageURL;
    private double latitude;
    private double longitude;
    private int timeUpdated;
    private int distance;
    private PlacePhoto placePhoto;

    public Place() {}

    public Place(String id, String name, String vicinity, String type, String img) {
        this.ID = id;
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

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public PlacePhoto getPhoto() { return placePhoto; }
    public void setPhoto(PlacePhoto placePhoto) {
        this.placePhoto = placePhoto;
    }
    public boolean hasPhoto() {
        return placePhoto != null;
    }
}

