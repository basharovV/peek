/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.main;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.peekapps.peek.presentation.ui.UIEventBus;
import com.peekapps.peek.presentation.ui.main.events.CameraSelectedEvent;
import com.peekapps.peek.presentation.ui.main.events.FeedSelectedEvent;
import com.peekapps.peek.presentation.ui.main.events.MainPagerEvent;
import com.peekapps.peek.presentation.ui.main.events.MapSelectedEvent;

import javax.inject.Inject;

/**
 * Created by Slav on 07/03/2016.
 */
public class MainScrollListener implements ViewPager.OnPageChangeListener {

    private MainView mainView;
    private int currentPage = 0;
    private UIEventBus<Object> uiEventBus;

    @Inject
    public MainScrollListener(UIEventBus uiEventBus) {
        super();
        this.uiEventBus = uiEventBus;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    //Handle status bar here (when page is selected, scroll not finished)
    public void onPageSelected(int position) {
        currentPage = position;
        uiEventBus.postEvent(new MainPagerEvent(position, MainPagerEvent.PAGE_SELECTED));
    }


    //Handle camera preview here (after scroll has stopped)
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            uiEventBus.postEvent(new MainPagerEvent(currentPage, MainPagerEvent.PAGE_IDLE));
        }
    }
}
