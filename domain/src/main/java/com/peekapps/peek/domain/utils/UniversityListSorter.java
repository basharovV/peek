/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.utils;

import com.peekapps.peek.domain.University;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Slav on 09/11/2015.
 */
public class UniversityListSorter {

    public static final int SORT_TYPE_POPULARITY = 0;
    public static final int SORT_TYPE_LAST_UPDATE = 1;
    public static final int SORT_TYPE_DISTANCE = 2;
    public static final int SORT_TYPE_FRIENDS = 3;

    /**
     * Use the Java Collections sort() to sort the array based on given criteria
     * @param placesList
     */
    public static void sort(List<University> placesList, int sortBy) {
        if (placesList != null && placesList.size() > 1) {

            //Initialise indexes
            switch (sortBy) {
                case SORT_TYPE_POPULARITY:
                    Collections.sort(placesList, new Comparator<University>() {
                        @Override
                        public int compare(University lhs, University rhs) {
                            return (Integer.compare(lhs.getNumberOfPhotos(),
                                    rhs.getNumberOfPhotos()));
                        }
                    });
                    break;
                case SORT_TYPE_LAST_UPDATE:
                    Collections.sort(placesList, new Comparator<University>() {
                        @Override
                        public int compare(University lhs, University rhs) {
                            return (Double.compare(lhs.getMinutesAgoUpdated(),
                                    rhs.getMinutesAgoUpdated()));
                        }
                    });
                    break;
                case SORT_TYPE_DISTANCE:
                    Collections.sort(placesList, new Comparator<University>() {
                        @Override
                        public int compare(University lhs, University rhs) {
                            return (Double.compare(lhs.getDistanceFromUser(), rhs.getDistanceFromUser()));
                        }
                    });
                    break;
            }
        }
    }
}
