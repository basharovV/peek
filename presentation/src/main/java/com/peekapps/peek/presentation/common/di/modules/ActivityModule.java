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
package com.peekapps.peek.presentation.common.di.modules;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


import com.peekapps.peek.data.permissions.PermissionUtils;
import com.peekapps.peek.data.permissions.PermissionUtilsImpl;
import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.interactor.GetUserDetails;
import com.peekapps.peek.domain.interactor.GetUserLocation;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.domain.repository.UserRepository;
import com.peekapps.peek.domain.services.LocationProvider;
import com.peekapps.peek.presentation.common.di.PerActivity;
import com.peekapps.peek.presentation.ui.BaseActivity;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity state and expose it to the graph.
 */
@Module
public class ActivityModule {
  private final AppCompatActivity activity;

  public ActivityModule(AppCompatActivity activity) {
    this.activity = activity;
  }

  /**
  * Expose the activity to dependents in the graph.
  */
  @Provides @PerActivity
  AppCompatActivity activity() {
    return this.activity;
  }

  @Provides @PerActivity @Named("supportFragmentManager")
  FragmentManager supportFragmentManager() {
    return activity.getSupportFragmentManager();
  }


  @Provides @PerActivity
  PermissionUtils permissionUtils(PermissionUtilsImpl permissionUtils) {
    return permissionUtils;
  }

  @Provides @PerActivity @Named("userLocation")
  Interactor getUserLocationUseCase(
          LocationProvider locationProvider, ThreadExecutor threadExecutor,
          PostExecutionThread postExecutionThread) {
    return new GetUserLocation(locationProvider, threadExecutor, postExecutionThread);
  }
}
