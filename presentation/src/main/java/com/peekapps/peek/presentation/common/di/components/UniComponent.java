/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.common.di.components;

import com.peekapps.peek.presentation.common.di.PerActivity;
import com.peekapps.peek.presentation.common.di.PerFragment;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.MapModule;
import com.peekapps.peek.presentation.common.di.modules.UniModule;
import com.peekapps.peek.presentation.ui.onboarding.UniSelectActivity;
import com.peekapps.peek.presentation.ui.uniprofile.UniProfileActivity;
import com.peekapps.peek.presentation.ui.uniprofile.UniProfileView;

import dagger.Component;

/**
 * Created by Slav on 02/03/2016.
 */
@PerActivity
@Component(dependencies = {ApplicationComponent.class},
        modules = {UniModule.class, ActivityModule.class})
public interface UniComponent {
    void inject(UniSelectActivity activity);
    void inject(UniProfileActivity activity);
}
