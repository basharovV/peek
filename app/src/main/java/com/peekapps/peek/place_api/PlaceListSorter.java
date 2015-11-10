package com.peekapps.peek.place_api;

import com.peekapps.peek.fragments.FeedFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Slav on 09/11/2015.
 */
public class PlaceListSorter {


    /**
     * Use the Java Collections sort() to sort the array based on given criteria
     * @param placesList
     */
    public static void sort(List<Place> placesList, int sortBy) {
        if (placesList != null && placesList.size() > 1) {

            //Initialise indexes
            switch (sortBy) {
                case FeedFragment.FEED_TYPE_POPULARITY:
                    Collections.sort(placesList, new Comparator<Place>() {
                        @Override
                        public int compare(Place lhs, Place rhs) {
                            return (Integer.compare(lhs.getNumberOfPhotos(),
                                    rhs.getNumberOfPhotos()));
                        }
                    });
                    break;
                case FeedFragment.FEED_TYPE_LAST_UPDATE:
                    Collections.sort(placesList, new Comparator<Place>() {
                        @Override
                        public int compare(Place lhs, Place rhs) {
                            return (Double.compare(lhs.getMinutesAgoUpdated(),
                                    rhs.getMinutesAgoUpdated()));
                        }
                    });
                    break;
                case FeedFragment.FEED_TYPE_DISTANCE:
                    Collections.sort(placesList, new Comparator<Place>() {
                        @Override
                        public int compare(Place lhs, Place rhs) {
                            return (Double.compare(lhs.getDistance(), rhs.getDistance()));
                        }
                    });
                    break;
            }
        }
    }
}
