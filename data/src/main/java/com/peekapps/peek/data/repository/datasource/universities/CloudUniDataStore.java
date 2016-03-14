/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.repository.datasource.universities;


import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UniEntity;
import com.peekapps.peek.data.net.RestApi;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * {@link UniDataStore} implementation based on connections to the api (Cloud).
 */
public class CloudUniDataStore implements UniDataStore {

  private final RestApi restApi;


  /**
   * Construct a {@link UniDataStore} based on connections to the api (Cloud).
   *
   * @param restApi The {@link RestApi} implementation to use.
   */
  public CloudUniDataStore(RestApi restApi) {
    this.restApi = restApi;
  }

  @Override
  public Observable<List<UniEntity>> uniEntityList() {
    return this.restApi.uniEntityList();
  }

//  @Override
//  public Observable<UniEntity> uniEntityDetails(final int uniId) {
//    return this.restApi.uniEntityById(uniId).doOnNext(saveToCacheAction);
//  }
}
