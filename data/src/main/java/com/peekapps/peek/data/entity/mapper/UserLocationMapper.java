/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.entity.mapper;

import android.location.Location;

import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UserEntity;
import com.peekapps.peek.domain.User;
import com.peekapps.peek.domain.UserLocation;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Based on Fernando Cejas Clean Architecture
 */

@Singleton
public class UserLocationMapper {

    @Inject
    public UserLocationMapper() {}

    /**
     * Transform a {@link LocationEntity} into an {@link UserLocation}.
     *
     * @param locationEntity Object to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public UserLocation transform(LocationEntity locationEntity) {
        UserLocation location = null;
        if (locationEntity != null) {
            location = new UserLocation();
            location.setLatitude(locationEntity.getLatitude());
            location.setLongitude(locationEntity.getLongitude());
            location.setCity(locationEntity.getCity());
            location.setCountry(locationEntity.getCountry());
            location.setWithinBounds(locationEntity.isWithinBounds());
        }

        return location;
    }

    public UserLocation transform(Location googleLocation) {
        UserLocation location = null;
        if (googleLocation != null) {
            location = new UserLocation();
            location.setLatitude(googleLocation.getLatitude());
            location.setLongitude(googleLocation.getLongitude());
            location.setCity("None");
            location.setCountry("None");
            location.setWithinBounds(false);
        }

        return location;
    }
}
