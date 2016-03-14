/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.location.observables;

import android.content.Context;

import com.google.android.gms.location.LocationServices;

/**
 * Created by Slav on 04/03/2016.
 */
public abstract class BaseLocationObservable<T> extends GoogleApiObservable<T> {
    protected BaseLocationObservable(Context ctx) {
        super(ctx, LocationServices.API);
    }
}
