/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.interactor;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.UserLocation;
import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.repository.UniRepository;
import com.peekapps.peek.domain.services.LocationProvider;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Slav on 10/03/2016.
 */
public class GetSuggestedUniversities extends Interactor{

    private final UniRepository uniRepository;
    private final LocationProvider locationProvider;

    @Inject
    public GetSuggestedUniversities(UniRepository uniRepository,
                                    LocationProvider locationProvider,
                                    ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.uniRepository = uniRepository;
        this.locationProvider = locationProvider;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        final UserLocation temp = new UserLocation();
        temp.setLatitude(55.9533);
        temp.setLongitude(-3.1883);
        return Observable.just(temp)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .switchMap(new Func1<UserLocation, Observable<List<University>>>() {
                    @Override
                    public Observable<List<University>> call(UserLocation userLocation) {
                        return uniRepository.suggestedUniversities(userLocation);
                    }
                })
                .limit(5);
//        return locationProvider.getLastLocation()
//                .switchIfEmpty(locationProvider.getLocationUpdates())
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .switchMap(new Func1<UserLocation, Observable<List<University>>>() {
//                    @Override
//                    public Observable<List<University>> call(UserLocation userLocation) {
//                        return uniRepository.suggestedUniversities(userLocation);
//                    }
//                })
//                .limit(5);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }
}
