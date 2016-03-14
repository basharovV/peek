/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.interactor;

import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.services.LocationProvider;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Slav on 04/03/2016.
 */
public class GetUserLocation extends Interactor {

    LocationProvider locationProvider;

    @Inject
    public GetUserLocation(LocationProvider locationProvider, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.locationProvider = locationProvider;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return locationProvider.getLocationUpdates();
    }


    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }
}
