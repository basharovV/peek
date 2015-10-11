package com.peekapps.peek;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by Slav on 07/10/2015.
 */
public class Animations {

    /**
     * Singleton pattern for animations - available to all classes.
     */
    private static Animations instance = null;

    public static final int ANIMATION_FADE_IN = 0;
    public static final int ANIMATION_FADE_OUT = 1;

    public static Animations getInstance() {
        if (instance == null) {
            return new Animations();
        }
        return instance;
    }

    protected Animations() {
    }

    public void fade(View view, int animation) {
        Animation fadeAnimation = new AlphaAnimation(0, 1);
        //TO BE DONE IN XML, programmatically for now
        switch(animation) {
            case ANIMATION_FADE_IN:
                fadeAnimation = new AlphaAnimation(0, 1);
                break;
            case ANIMATION_FADE_OUT:
                fadeAnimation = new AlphaAnimation(1, 0);
                break;
        }
        fadeAnimation.setInterpolator(new AccelerateInterpolator());
        fadeAnimation.setDuration(300);
        view.setAnimation(fadeAnimation);
        fadeAnimation.start();
    }
}
