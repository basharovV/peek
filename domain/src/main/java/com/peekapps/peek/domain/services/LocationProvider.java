/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.services;
import com.peekapps.peek.domain.UserLocation;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Slav on 03/03/2016.
 */
public interface LocationProvider {

    Observable<UserLocation> getLastLocation();
    Observable<UserLocation> getLocationUpdates();

}
