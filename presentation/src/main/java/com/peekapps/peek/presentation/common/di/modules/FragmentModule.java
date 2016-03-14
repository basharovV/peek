/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.common.di.modules;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.peekapps.peek.presentation.common.di.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Slav on 03/03/2016.
 */
@Module
public class FragmentModule {

    Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides @PerFragment @Named("childFragmentManager")
    public FragmentManager childFragmentManager() {
        return fragment.getChildFragmentManager();
    }

}
