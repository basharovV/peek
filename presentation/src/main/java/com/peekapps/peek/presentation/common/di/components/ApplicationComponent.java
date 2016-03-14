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
package com.peekapps.peek.presentation.common.di.components;

import android.content.Context;

import com.peekapps.peek.data.permissions.PermissionUtils;
import com.peekapps.peek.domain.executor.*;
import com.peekapps.peek.domain.repository.PlaceRepository;
import com.peekapps.peek.domain.repository.UniRepository;
import com.peekapps.peek.domain.repository.UserRepository;
import com.peekapps.peek.domain.services.LocationProvider;
import com.peekapps.peek.presentation.common.di.modules.ApplicationModule;
import com.peekapps.peek.presentation.common.navigation.Navigator;
import com.peekapps.peek.presentation.ui.BaseActivity;
import com.peekapps.peek.presentation.ui.UIEventBus;
import com.peekapps.peek.presentation.ui.login.LoginActivity;
import com.peekapps.peek.presentation.ui.main.events.MainPagerEvent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs.
    Context context();
    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
//    PlaceRepository placeRepository();
    UserRepository userRepository();
    UniRepository uniRepository();
    LocationProvider locationProvider();
    Navigator navigator();
    UIEventBus uiEventBus();
}
