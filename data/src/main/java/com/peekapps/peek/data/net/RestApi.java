/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peekapps.peek.data.net;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UniEntity;
import com.peekapps.peek.data.entity.UserEntity;
import com.peekapps.peek.domain.UserLocation;

import java.util.List;

import rx.Observable;

/**
 * RestApi for retrieving data from the network.
 */
public interface RestApi {
  String API_BASE_URL = "https://peekuni.firebaseio.com/";

  /** Api url for getting all users */
  String API_URL_GET_USER_LIST = API_BASE_URL + "users.json";
  /** Api url for getting a user profile: Remember to concatenate id + 'json' */
  String API_URL_GET_USER_DETAILS = API_BASE_URL + "user_";

  String API_URL_GET_UNI_LIST = API_BASE_URL + "universities.json";

  /**
   * Retrieves an {@link rx.Observable} which will emit a List of {@link UserEntity}.
   */
  Observable<List<UserEntity>> userEntityList();

  /**
   * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
   *
   * @param userId The user id used to get user data.
   */
  Observable<UserEntity> userEntityById(final int userId);

  Observable<List<UniEntity>> uniEntityList();

  Observable<UniEntity> suggestedUniEntityList(UserLocation userLocation);
}
