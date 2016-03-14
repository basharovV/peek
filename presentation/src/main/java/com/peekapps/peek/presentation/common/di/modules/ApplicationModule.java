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

import android.content.Context;


import com.peekapps.peek.data.cache.UserCache;
import com.peekapps.peek.data.cache.UserCacheImpl;
import com.peekapps.peek.data.executor.JobExecutor;
import com.peekapps.peek.data.location.LocationProviderImpl;
import com.peekapps.peek.data.permissions.PermissionUtils;
import com.peekapps.peek.data.permissions.PermissionUtilsImpl;
import com.peekapps.peek.data.repository.UniDataRepository;
import com.peekapps.peek.data.repository.UserDataRepository;
import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.repository.UniRepository;
import com.peekapps.peek.domain.repository.UserRepository;
import com.peekapps.peek.domain.services.LocationProvider;
import com.peekapps.peek.presentation.PeekApplication;
import com.peekapps.peek.presentation.UIThread;
import com.peekapps.peek.presentation.common.navigation.Navigator;
import com.peekapps.peek.presentation.ui.UIEventBus;
import com.peekapps.peek.presentation.ui.main.events.MainPagerEvent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
  private final PeekApplication application;

  public ApplicationModule(PeekApplication application) {
    this.application = application;
  }

  @Provides @Singleton
  Context applicationContext() {
    return this.application;
  }

  @Provides @Singleton
  Navigator navigator() {
    return new Navigator();
  }

  @Provides @Singleton
  ThreadExecutor threadExecutor(JobExecutor jobExecutor) {
    return jobExecutor;
  }

  @Provides @Singleton
  PostExecutionThread postExecutionThread(UIThread uiThread) {
    return uiThread;
  }

  @Provides @Singleton
  UserCache userCache(UserCacheImpl userCache) {
    return userCache;
  }

  @Provides @Singleton
  UserRepository userRepository(UserDataRepository userDataRepository) {
    return userDataRepository;
  }

  @Provides @Singleton
  UniRepository uniRepository(UniDataRepository uniDataRepository) {
    return uniDataRepository;
  }

  @Provides @Singleton
  LocationProvider locationProvider(LocationProviderImpl locationProvider) {
    return locationProvider;
  }

  @Provides @Singleton
  UIEventBus pagerUIEventBus() {
    return new UIEventBus<>();
  }

}
