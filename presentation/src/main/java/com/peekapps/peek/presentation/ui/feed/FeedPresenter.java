/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.util.Log;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.interactor.DefaultSubscriber;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.domain.utils.*;
import com.peekapps.peek.presentation.Presenter;
import com.peekapps.peek.presentation.ui.UIEventBus;
import com.peekapps.peek.presentation.ui.main.events.FeedSelectedEvent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Slav on 11/03/2016.
 */
public class FeedPresenter implements Presenter {

    private FeedView feedView;

    // ------------- Sorting attributes --------------

    public int currentSortType = 0;

    public static final int POSITION_SORT_BAR = 0;
    public static final int POSITION_SEARCH_BAR = 1;

    private List<University> universityList;

    UIEventBus<FeedSelectedEvent> uiEventBus;

    private Interactor getUniversitiesInteractor;

    @Inject
    public FeedPresenter(@Named("universities") Interactor getUniversitiesInteractor,
                          UIEventBus uiEventBus) {
        this.getUniversitiesInteractor = getUniversitiesInteractor;
        this.uiEventBus = uiEventBus;
    }

    public void initialize() {
        loadUniversities();
    }


    public void setFeedView(FeedView feedView) {
        this.feedView = feedView;
    }


    public void updateFeed() {
        // check if different sorting, location...
        // get new university list
        loadUniversities();
    }

    // ---------- Data related ---------

    // View updates
    public void showUniversitiesInView(List<University> universityList) {
        feedView.updateFeed(universityList);
    }

    public void sortFeedBy(int criteria) {
        //Call the sorting method
        UniversityListSorter.sort(universityList, criteria);
    }

    // Data fetching

    public void loadUniversities() {
        getUniversitiesInteractor.execute(new OnUniversitiesReadySubscriber());
    }

    private class OnUniversitiesReadySubscriber extends DefaultSubscriber<List<University>> {
        @Override
        public void onCompleted() {
            Log.d("MapPresenter", "Univesities ready!");
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }

        @Override
        public void onNext(final List<University> universities) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showUniversitiesInView(universities);
                }
            }).start();
        }
    }
    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
