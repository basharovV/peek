/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.repository;

import com.peekapps.peek.domain.LatLngBounds;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.UserLocation;

import java.util.List;

import rx.Observable;

/**
 * Created by Slav on 10/03/2016.
 */
public interface UniRepository {

    public Observable<List<University>> universities();
    public Observable<List<University>> suggestedUniversities(UserLocation userLocation);
}
