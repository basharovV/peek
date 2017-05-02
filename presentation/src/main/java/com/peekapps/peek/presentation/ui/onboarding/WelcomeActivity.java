/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Slav on 19/04/2016.
 */
public class WelcomeActivity extends BaseActivity {

    // UI components
    @Bind(R.id.welcomeFrame)            View welcomeFrame;
    @Bind(R.id.welcomeGradCap)          ImageView welcomeCap;
    @Bind(R.id.welcomeLogo)             ImageView welcomeLogo;
    @Bind(R.id.welcomeStartButton)      FrameLayout startButton;

    /**
     * Components for the animations for the graduation cap and the 'P' logo.
     * Uses the Rebound library and its spring mechanics.
     */
    private final BaseSpringSystem springSystem = SpringSystem.create();
    private final CapDropSpringListener capDropListener = new CapDropSpringListener();
    private final CapRotateSpringListener capRotateListener = new CapRotateSpringListener();
    private final LogoBounceSpringListener logoBounceListener = new LogoBounceSpringListener();
    private final CapBounceSpringListener capBounceListener = new CapBounceSpringListener();
    private Spring capDropSpring;
    private Spring capBounceSpring;
    private Spring capRotateSpring;
    private Spring logoBounceSpring;


    // To keep track of states for various animations
    private int[] logoLocation;
    private boolean capAtReadyPos = false;
    private boolean logoAtBottom = false;
    private boolean welcomeDropCompleted = false;
    private static final int CAP_ROTATION = 32;
    private float capFinalTranslationX = 0;
    private float capFinalTranslationY = 0;

    // User interaction components - hidden easter egg :)
    GestureDetector gestureDetector;
    /**
     * Need the rectangle of the logo to check if a touch event originated from here.
     * If touch event is outside of the rect, 'bounce' the logo back up.
     */
    private Rect logoRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initializeActivity();
    }

    /*
        Need to initialise cap position on this method call -
        when the view is placed in its parent, and the screen
        location can be determined.
         */
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasFocus && !capAtReadyPos)   {
            calculatePositions();
            initCap();
        }

    }

    private void initializeActivity() {
        // Init springs for cap animations
        capDropSpring = springSystem.createSpring();
        capRotateSpring = springSystem.createSpring();
        logoBounceSpring = springSystem.createSpring();
        capBounceSpring = springSystem.createSpring();

        /**
         * Different tension and friction values for springs
         * The cap has a lower tension, and lower friction than the logo.
         */
        SpringConfig capConfig = new SpringConfig(90, 10);
        capDropSpring.setSpringConfig(capConfig);
        SpringConfig bounceConfig = new SpringConfig(100, 13);
        logoBounceSpring.setSpringConfig(bounceConfig);

        // Detect gestures anywhere on screen
        gestureDetector = new GestureDetector(this, new LogoInteractionListener());
        welcomeLogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                logoRect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_OUTSIDE
                        || action == MotionEvent.ACTION_CANCEL) {
                    logoBounceSpring.setEndValue(0);
                }

                return gestureDetector.onTouchEvent(event);
            }
        });

        //
        if (Build.VERSION.SDK_INT < 21) {
            setCompatUI();
        }
    }

    private void setCompatUI() {
        startButton.setBackgroundResource(R.drawable.welcome_bg_start_button_ripple);
    }

    @OnClick(R.id.welcomeStartButton)
    protected void onStartButtonClick() {
        startTutorialTransition();
    }

    private void calculatePositions() {
        logoLocation = new int[2];
        welcomeLogo.getLocationInWindow(logoLocation);
    }

    private void initCap() {
        int[] logoLocation = new int[2];
        welcomeLogo.getLocationInWindow(logoLocation);

        // Need this value for resuming activity - NOT ANYMORE

        // Final cap X translation (Y translation defined by Spring)
        capFinalTranslationX = logoLocation[1] + (welcomeLogo.getWidth() / 8);
        welcomeCap.setTranslationX(capFinalTranslationX);
        welcomeCap.setTranslationY(-welcomeCap.getHeight());
        capAtReadyPos = true;
    }

    private void dropCap() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                capDropSpring.setEndValue(1);
            }
        }, 500);
    }


    private void setFinalCapState() {
        welcomeCap.setTranslationY(capFinalTranslationY);
        welcomeCap.setTranslationX(capFinalTranslationX);
        welcomeCap.setRotation(CAP_ROTATION);
    }


    // ---------- User gesture handling -----------


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_OUTSIDE
                || action == MotionEvent.ACTION_CANCEL) {
            logoBounceSpring.setEndValue(0);
            capBounceSpring.setEndValue(0);
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            if (logoRect != null) {
                if (!logoRect.contains(welcomeLogo.getLeft() + (int) event.getX(), welcomeLogo.getTop() + (int) event.getY())) {
                    // Outside of bounds
                    logoBounceSpring.setEndValue(0);
                    capBounceSpring.setEndValue(0);
                }
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private class LogoInteractionListener implements GestureDetector.OnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (logoRect != null) {
                if (logoRect.contains(welcomeLogo.getLeft() + (int) e1.getX(), welcomeLogo.getTop() + (int) e1.getY())) {
                    // Outside of bounds
                    capBounceSpring.setEndValue(1);
                    logoBounceSpring.setEndValue(1);
                }
                if (logoRect.contains(welcomeLogo.getLeft() + (int) e2.getX(), welcomeLogo.getTop() + (int) e2.getY())) {
                    logoBounceSpring.setEndValue(0);
                    capBounceSpring.setEndValue(0);
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (logoRect != null) {
                if (logoRect.contains(welcomeLogo.getLeft() + (int) e1.getX(), welcomeLogo.getTop() + (int) e1.getY())) {
                    // Outside of bounds
                    logoBounceSpring.setEndValue(1);
                    capBounceSpring.setEndValue(1);
                }
                if (logoRect.contains(welcomeLogo.getLeft() + (int) e2.getX(), welcomeLogo.getTop() + (int) e2.getY())) {
                    logoBounceSpring.setEndValue(0);
                    capBounceSpring.setEndValue(0);
                }
            }
            return false;
        }
    }

    // --------------- Spring listeners for controlling the 'cap drop' animation -----------

    private class CapDropSpringListener extends SimpleSpringListener {

        boolean hit = false;
        @Override
        public void onSpringUpdate(Spring spring) {

            if (!welcomeDropCompleted) {
                float stoppingPoint = logoLocation[1] - (welcomeLogo.getHeight() / 3);
                float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, -welcomeCap.getHeight(), stoppingPoint);
                welcomeCap.setTranslationY(mappedValue);
            }
            if (spring.isOvershooting() && !hit) {
                logoBounceSpring.setEndValue(1.0);
                hit = true;
            }
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            super.onSpringEndStateChange(spring);
            capRotateSpring.setEndValue(1);
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            super.onSpringAtRest(spring);
            if (!welcomeDropCompleted) {
                capFinalTranslationY = welcomeCap.getTranslationY();
                Log.d("Welcome", "TranslationXY" + String.valueOf(capFinalTranslationX) + ", " +
                    String.valueOf(capFinalTranslationY));
                welcomeDropCompleted = true;
            }
        }
    }


    private class CapBounceSpringListener extends SimpleSpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {
            float stoppingPoint = capFinalTranslationY + (welcomeLogo.getHeight() / 7);
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, capFinalTranslationY, stoppingPoint);
            if (welcomeDropCompleted) {
                welcomeCap.setTranslationY(mappedValue);
            }
        }

    }

    private class CapRotateSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int stoppingPoint = CAP_ROTATION;
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, stoppingPoint);
            welcomeCap.setRotation(mappedValue);
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            super.onSpringEndStateChange(spring);
            if (spring.isAtRest()) {
                capRotateSpring.setEndValue(1);
            }
        }
    }

    private class LogoBounceSpringListener extends SimpleSpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {
            float displacement = welcomeLogo.getHeight() / 10;
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 0, displacement);

            welcomeLogo.setTranslationY(mappedValue);

            if (spring.isOvershooting() && !logoAtBottom) {
                logoBounceSpring.setEndValue(0);
                logoAtBottom = true;
            }
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            super.onSpringEndStateChange(spring);
        }

    }

    private void startTutorialTransition() {
        // get the center for the clipping circle
        int cx = logoLocation[0] + welcomeLogo.getWidth() / 2;
        int cy = logoLocation[1] + welcomeLogo.getHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = welcomeFrame.getWidth();

        // create the animation (the final radius is zero)
        if (Build.VERSION.SDK_INT > 20) {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(welcomeFrame, cx, cy, initialRadius, 0);
            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    welcomeFrame.setVisibility(View.INVISIBLE);
                    goToTutorial();
                }
            });

            // start the animation
            anim.setDuration(1000);
            anim.start();
        }



    }

    private void goToTutorial() {
        // Go to tutorial
        Intent tutorialIntent = new Intent(WelcomeActivity.this, TutorialActivity.class);
        if (Build.VERSION.SDK_INT > 20) {
            View sharedLogo = welcomeLogo;
            String transitionName = getString(R.string.tutorial_logo_transition);

            // Put cap position, rotation - for animation on next screen
            tutorialIntent.putExtra("capTranslationX", capFinalTranslationX);
            tutorialIntent.putExtra("capTranslationY", capFinalTranslationY);
            tutorialIntent.putExtra("capRotation", CAP_ROTATION);

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this,
                    sharedLogo, transitionName);
            startActivity(tutorialIntent, activityOptions.toBundle());
        }
        else startActivity(tutorialIntent);
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (capFinalTranslationY > 0) {
            setFinalCapState();
        }
        capDropSpring.addListener(capDropListener);
        capBounceSpring.addListener(capBounceListener);
        capRotateSpring.addListener(capRotateListener);
        logoBounceSpring.addListener(logoBounceListener);
        dropCap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capRotateSpring.removeListener(capRotateListener);
        capDropSpring.removeListener(capDropListener);
        logoBounceSpring.removeListener(logoBounceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
