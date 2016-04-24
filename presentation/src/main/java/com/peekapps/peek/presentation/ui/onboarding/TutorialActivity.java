/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.ActivityComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerActivityComponent;
import com.peekapps.peek.presentation.ui.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Slav on 20/04/2016.
 */
public class TutorialActivity extends BaseActivity {

    @Bind(R.id.tutorialPager)           ViewPager tutorialPager;
    @Bind(R.id.tutorialIndicator)       CircleIndicator tutorialIndicator;

    @Inject
    TutorialPagerAdapter tutorialPagerAdapter;

    ActivityComponent tutorialComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial_activity);
        ButterKnife.bind(this);
        initializeComponents();
        initializeActivity();
    }


    private void initializeComponents() {
        tutorialComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
        tutorialComponent.inject(this);
    }

    private void initializeActivity() {
        tutorialPager.setAdapter(tutorialPagerAdapter);
        tutorialIndicator.setViewPager(tutorialPager);

        if (Build.VERSION.SDK_INT > 20) {
            Transition activityFade = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
            activityFade.excludeTarget(android.R.id.statusBarBackground, true);
            activityFade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setAllowEnterTransitionOverlap(false);
            getWindow().setEnterTransition(activityFade);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}