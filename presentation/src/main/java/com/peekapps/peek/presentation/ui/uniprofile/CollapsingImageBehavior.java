/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.uniprofile;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.peekapps.peek.presentation.R;

/**
 * Created by Slav on 23/05/2016.
 */
public class CollapsingImageBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    private Context context;

    private static final float PERCENTAGE_SIZE_MIN = 0.3f;
    private static final float MIN_SCALE = 0.3f;

    private float avatarMaxSize;
    private float finalLeftAvatarPadding;
    private float startPosition;

    private int maxPadding;
    private int paddingDecrease;

    private float customFinalHeight;
    private int startXPosition;
    private float startToolbarPosition;
    private int startYPosition;
    private int finalYPosition;
    private int startHeight;
    private int finalXPosition;
    private float changeBehaviorPoint;

    public CollapsingImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingImageBehavior);
            customFinalHeight = a.getDimension(R.styleable.CollapsingImageBehavior_finalHeight, 0);
            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {

        return dependency instanceof CollapsingToolbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        initProperties(child, dependency);
        Log.d("Collapse", "test");
        final int maxScrollDistance = (int) (startToolbarPosition);
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        if (expandedPercentageFactor < changeBehaviorPoint) {
            float heightFactor = (changeBehaviorPoint - expandedPercentageFactor) / changeBehaviorPoint;

            float distanceXToSubtract = ((startXPosition - finalXPosition)
                    * heightFactor) + (child.getHeight()/2);
            float distanceYToSubtract = ((startYPosition - finalYPosition)
                    * (1f - expandedPercentageFactor)) + (child.getHeight()/2);

            child.setX(startXPosition - distanceXToSubtract);
            child.setY(startYPosition - distanceYToSubtract);

            //padding
            int newPadding = paddingDecrease * (int) heightFactor;
            child.setPadding(newPadding, newPadding, newPadding, newPadding);

            float heightToSubtract = ((startHeight - customFinalHeight) * heightFactor);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (startHeight - heightToSubtract);
            lp.height = (int) (startHeight - heightToSubtract);

            child.setLayoutParams(lp);
        } else {
            float distanceYToSubtract = ((startYPosition - finalYPosition)
                    * (1f - expandedPercentageFactor)) + (startHeight/2);

            child.setX(startXPosition - child.getWidth()/2);
            child.setY(startYPosition - distanceYToSubtract);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (startHeight);
            lp.height = (int) (startHeight);
            child.setLayoutParams(lp);
        }
        return true;
    }
    
    private void initProperties(FrameLayout child, View dependency) {
        if (startYPosition == 0)
            startYPosition = (int) (dependency.getY());

        if (finalYPosition == 0)
            finalYPosition = (dependency.getHeight() /2);

        if (startHeight == 0)
            startHeight = child.getHeight();

        if (startXPosition == 0)
            startXPosition = (int) (child.getX() + (child.getWidth() / 2));

        if (finalXPosition == 0)
            finalXPosition = context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + ((int) customFinalHeight / 2);

        if (startToolbarPosition == 0)
            startToolbarPosition = dependency.getY();

        if (changeBehaviorPoint == 0) {
            changeBehaviorPoint = (child.getHeight() - customFinalHeight) / (2f * (startYPosition - finalYPosition));
        }

        maxPadding = child.getPaddingTop();
        paddingDecrease = (int) (maxPadding - (maxPadding * MIN_SCALE));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
