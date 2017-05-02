/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.repository;

import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.android.gms.maps.model.LatLng;
import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UniEntity;
import com.peekapps.peek.data.entity.mapper.UniEntityDataMapper;
import com.peekapps.peek.data.location.LocationUtils;
import com.peekapps.peek.data.repository.datasource.universities.UniDataStore;
import com.peekapps.peek.data.repository.datasource.universities.UniDataStoreFactory;
import com.peekapps.peek.domain.LatLngBounds;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.UserLocation;
import com.peekapps.peek.domain.repository.UniRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Slav on 10/03/2016.
 */
@Singleton
public class UniDataRepository implements UniRepository {

    private final UniDataStoreFactory uniDataStoreFactory;
    private final UniEntityDataMapper uniEntityDataMapper;

    /**
     * Constructs a {@link UniRepository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     * @param uniEntityDataMapper {@link UniEntityDataMapper}.
     */
    @Inject
    public UniDataRepository(UniDataStoreFactory dataStoreFactory,
                              UniEntityDataMapper uniEntityDataMapper) {
        this.uniDataStoreFactory = dataStoreFactory;
        this.uniEntityDataMapper = uniEntityDataMapper;
    }

    @SuppressWarnings("Convert2MethodRef")
    @RxLogObservable
    @Override
    public Observable<List<University>> universities() {
        //we always get all unis from the cloud
        final UniDataStore uniDataStore = this.uniDataStoreFactory.createCloudDataStore();
        return uniDataStore.uniEntityList()
                .map(new Func1<List<UniEntity>, List<University>>() {
                    @Override
                    public List<University> call(List<UniEntity> uniEntities) {
                        return uniEntityDataMapper.transform(uniEntities);
                    }
                });
    }

    @SuppressWarnings("Convert2MethodRef")
    @RxLogObservable
    @Override
    public Observable<List<University>> suggestedUniversities(UserLocation userLocation) {

        // TEMPORARY : filter all universities to bounding box in here
        // (instead of getting only nearby unis from backend)
        final com.google.android.gms.maps.model.LatLngBounds bounds = LocationUtils.suggestedUniBounds(
                new LatLng(userLocation.getLatitude(), userLocation.getLongitude()),
                50000       //50km radius for suggested universities
        );

        //we always get all unis from the cloud
        final UniDataStore uniDataStore = this.uniDataStoreFactory.createCloudDataStore();
        return uniDataStore.suggestedUniEntityList(userLocation)
                // TEMPORARY!
                .filter(new Func1<UniEntity, Boolean>() {
                    @Override
                    public Boolean call(UniEntity uniEntity) {
                        return uniEntity.isWithinBounds(bounds);
                    }
                })
                .toList()
                .map(new Func1<List<UniEntity>, List<University>>() {
                    @Override
                    public List<University> call(List<UniEntity> uniEntities) {
                        return uniEntityDataMapper.transform(uniEntities);
                    }
                });
    }
}
