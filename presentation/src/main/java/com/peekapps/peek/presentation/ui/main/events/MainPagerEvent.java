/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.main.events;
import android.support.v4.view.ViewPager.OnPageChangeListener;
/**
 * Created by Slav on 07/03/2016.
 */
public class MainPagerEvent {
    /**
    Indicates that the page was selected from {@link OnPageChangeListener#onPageSelected(int)}
     This can fire before the page is fully visible - as a result of a fling.
     **/
    public static int PAGE_SELECTED = 0;
    /**
    Indicates that the page was selected, is visible on screen and is currently idle. This event
     will happen after {@link #PAGE_SELECTED} as a result of a call from
     {@link OnPageChangeListener#onPageScrollStateChanged(int)} where the state is IDLE.
     **/
    public static int PAGE_IDLE = 1;

    private int pageState;
    private int position;

    public MainPagerEvent(int position, int pageState) {
        this.position = position;
        this.pageState = pageState;
    }

    public int getPageState() {
        return pageState;
    }

    public int getPosition() {
        return position;
    }

}
