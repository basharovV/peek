/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.location.observables;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Observer;

/**
 * From mcharmas Android-ReactiveLocation
 * https://github.com/mcharmas/Android-ReactiveLocation
 */

public class LastKnownLocationObservable extends BaseLocationObservable<Location> {

    private static final String TAG = LastKnownLocationObservable.class.getSimpleName();

    public static Observable<Location> createObservable(Context ctx) {
        return Observable.create(new LastKnownLocationObservable(ctx));
    }

    private LocationListener listener;

    private LastKnownLocationObservable(Context ctx) {
        super(ctx);
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super Location> observer) {
        try {
            observer.onNext(LocationServices.FusedLocationApi.getLastLocation(apiClient));
        }
        catch (SecurityException e) {
            observer.onError(e);
        }
    }

    @Override
    protected void onUnsubscribed(GoogleApiClient locationClient) {
    }

}
