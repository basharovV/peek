/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Slav on 17/03/2016.
 */
public class LocationSelectorBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    Context context;

    public LocationSelectorBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof RecyclerView; //maybe change to custom view
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        int feedScrollY = ((RecyclerView) dependency).getScrollY();
        if (feedScrollY < 0) {

        }
        int barTranslationY = 0;
        float percentComplete = -barTranslationY / dependency.getHeight();
        float scaleFactor = 1 - percentComplete;

        child.setScaleX(scaleFactor);
        child.setScaleY(scaleFactor);
        return false;
    }

    private float getToolbarTranslationY() {
        return 0f;
    }

    private int getStatusBarHeight() {
        //Set up height of status bar background (empty recycler view)
        int statusBarHeight = 0;
        int resourceId = context.getResources().
                getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
