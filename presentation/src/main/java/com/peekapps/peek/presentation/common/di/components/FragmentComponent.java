/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.common.di.components;

import android.app.Activity;

import com.peekapps.peek.presentation.common.di.PerFragment;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.FragmentModule;
import com.peekapps.peek.presentation.common.di.modules.UniModule;
import com.peekapps.peek.presentation.ui.BaseFragment;
import com.peekapps.peek.presentation.ui.camera.CameraFragment;
import com.peekapps.peek.presentation.ui.feed.FeedFragment;
import com.peekapps.peek.presentation.ui.map.MapFragment;

import dagger.Component;

/**
 * Created by Slav on 03/03/2016.
 */
@PerFragment
@Component(dependencies = {ApplicationComponent.class},
        modules = {FragmentModule.class, UniModule.class, ActivityModule.class})
public interface FragmentComponent {
    void inject(BaseFragment fragment);
    void inject(MapFragment mapFragment);
    void inject(CameraFragment cameraFragment);
    void inject(FeedFragment feedFragment);
}
