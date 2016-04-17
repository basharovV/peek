/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.map;

import android.location.Location;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.model.PlaceModel;
import com.peekapps.peek.presentation.ui.BaseView;

/**
 * Created by Slav on 02/03/2016.
 */
public interface MapPlacesView extends BaseView {
    void displayMarker(University place);
    void animateTo(double latitude, double longitude);
    void renderPlaceDetails(University placeModel);
    void showToastMessage(String message);
    void collapsePlacePanel();
}
