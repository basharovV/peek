/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.repository.datasource.universities;


import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UniEntity;
import com.peekapps.peek.data.entity.UserEntity;
import com.peekapps.peek.domain.LatLngBounds;
import com.peekapps.peek.domain.UserLocation;

import java.util.List;

import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface UniDataStore {
  /**
   * Get an {@link Observable} which will emit a List of {@link UserEntity}.
   */
  Observable<List<UniEntity>> uniEntityList();

  /**
   * Get an (@link Observable) for suggested Universities for the given location
   *
   * @param userLocation The coordinates to retrieve universities for
   */
  Observable<UniEntity> suggestedUniEntityList(UserLocation userLocation);

  /**
   * Get an {@link Observable} which will emit a {@link UserEntity} by its id.
   *
   * @param userId The id to retrieve user data.
   */
//  Observable<UserEntity> uniEntityDetails(final int userId);
}
