package com.peekapps.peek;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

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

    public static void crossfade(final View view1, final View view2) {
        view2.setAlpha(1f);
        view2.setVisibility(View.VISIBLE);
        view2.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view1.setAlpha(0f);
                        view1.setVisibility(View.VISIBLE);
                        view1.animate()
                                .alpha(1f)
                                .setInterpolator(new AccelerateInterpolator())
                                .setDuration(400);
                    }
                });
    }

    public static void fadeOut(final View view) {
        view.setAlpha(1f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        view.setAlpha(0f);
                    }
                });

    }

    public static void fadeIn(final View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(1f);
                    }
                });
    }

    public static void fade(final View view, final int anim, int offset) {
        Animation fadeAnimation = new AlphaAnimation(0f, 1f);
        //TO BE DONE IN XML, programmatically for now
        switch(anim) {
            case ANIMATION_FADE_IN:
                view.setAlpha(0f);
                view.setVisibility(View.VISIBLE);
                view.animate()
                        .alpha(1f)
                        .setInterpolator(new AccelerateInterpolator())
                        .setStartDelay(offset)
                        .setDuration(400);

                break;
            case ANIMATION_FADE_OUT:
                view.animate()
                        .alpha(0f)
                        .setInterpolator(new AccelerateInterpolator())
                        .setStartDelay(offset)
                        .setDuration(400)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                view.setVisibility(View.GONE);
                            }
                        });
                break;
        }
    }

    public void rotate180(View view) {
        view.animate().rotationBy(180);
    }
}
