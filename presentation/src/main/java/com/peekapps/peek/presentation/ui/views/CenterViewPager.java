/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.feed.TextFocusPagerAdapter;


/**
 * Created by Slav on 12/11/2015.
 */
public class CenterViewPager extends ViewPager {

    private String TAG = "CenterViewPager";
    private Activity activity;

    private int currentPage = 0;


    private Context context;

    public CenterViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public CenterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);
//        this.setPageTransformer(false, new TextFocusPageTransformer());
        this.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.uni_select_center_pager_margin));
        this.setOffscreenPageLimit(4);
//        this.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.area_selector_pager_margin));
    }


    public void initialize(Activity activity) {
        this.activity = activity;
    }

    }


