/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.repository.datasource.universities;

import android.content.Context;

import com.peekapps.peek.data.cache.UserCache;
import com.peekapps.peek.data.entity.mapper.UniEntityJsonMapper;
import com.peekapps.peek.data.entity.mapper.UserEntityJsonMapper;
import com.peekapps.peek.data.net.RestApi;
import com.peekapps.peek.data.net.RestApiImpl;
import com.peekapps.peek.data.repository.datasource.user.DiskUserDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory that creates different implementations of {@link UniDataStore}.
 */
@Singleton
public class UniDataStoreFactory {

  private final Context context;
  private final UserCache userCache;

  @Inject
  public UniDataStoreFactory(Context context, UserCache userCache) {
    if (context == null || userCache == null) {
      throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
    }
    this.context = context.getApplicationContext();
    this.userCache = userCache;
  }

  /**
   * Create {@link UniDataStore} from a user id.
   */
  public UniDataStore create(int userId) {
    UniDataStore userDataStore;
    userDataStore = createCloudDataStore();
    return userDataStore;
  }

  /**
   * Create {@link UniDataStore} to retrieve data from the Cloud.
   */
  public UniDataStore createCloudDataStore() {
    UniEntityJsonMapper uniEntityJsonMapper = new UniEntityJsonMapper();
    RestApi restApi = new RestApiImpl(this.context, uniEntityJsonMapper);

    return new CloudUniDataStore(restApi);
  }
}
