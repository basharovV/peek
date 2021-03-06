/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */
package com.peekapps.peek.data.repository.datasource.user;


import com.peekapps.peek.data.entity.UserEntity;

import java.util.List;

import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface UserDataStore {
  /**
   * Get an {@link rx.Observable} which will emit a List of {@link UserEntity}.
   */
  Observable<List<UserEntity>> userEntityList();

  /**
   * Get an {@link rx.Observable} which will emit a {@link UserEntity} by its id.
   *
   * @param userId The id to retrieve user data.
   */
  Observable<UserEntity> userEntityDetails(final int userId);
}
