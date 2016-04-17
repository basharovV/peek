
/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.utils.*;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.DaggerFragmentComponent;
import com.peekapps.peek.presentation.common.di.components.FragmentComponent;
import com.peekapps.peek.presentation.common.di.modules.FragmentModule;
import com.peekapps.peek.presentation.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedFragment extends BaseFragment implements FeedView{

    //------ Views
    @Bind(R.id.feedOptionsBarHolder)    LinearLayout optionsBarHolder;
    @Bind(R.id.feedSortSpinner)         AppCompatSpinner sortSpinner;
    @Bind(R.id.feedSearchButton)        ImageView searchButton;

    // Header
    @Bind(R.id.feedLocationSelectorLayout) LinearLayout locationSelectorLayout;
    @Bind(R.id.feedLocationSelector)    TextFocusViewPager locationSelectorPager;

    // Options bar


    // Main content
    @Bind(R.id.feedRefreshLayout)       SwipeRefreshLayout refreshLayout;
    @Bind(R.id.feedRecyclerView)        RecyclerView recyclerView;

    // Footer
    @Bind(R.id.feedFavouritesButton)    ImageView favouritesButton;
    @Bind(R.id.feedWorldButton)         ImageView worldButton;
    @Bind(R.id.feedMyUniButton)         ImageView myUniButton;

    // ------Components

    // Header
    @Inject
    TextFocusPagerAdapter locationSelectorAdapter;

    @Inject
    CardAdapter cardAdapter;
    private LinearLayoutManager layoutManager;
    private RefreshListener refreshListener;
    private AdapterView.OnItemSelectedListener sortTypeSelectedListener;

    @Inject
    FeedPresenter feedPresenter;

    FragmentComponent fragmentComponent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, rootView);
        initializeComponents();
        initializeFragment();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedPresenter.setFeedView(this);
        feedPresenter.initialize();
    }

    private void initializeFragment() {
        // Header
//        locationSelectorLayout.setTranslationY(getStatusBarHeight());
        locationSelectorPager.setAdapter(locationSelectorAdapter);
        // Options bar
//        optionsBarHolder.setTranslationY(getStatusBarHeight());
        //Recycler view
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new ScrollListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);

        //Refresh layout
        refreshLayout.setColorSchemeColors(Color.parseColor("#FFAE00"), Color.parseColor("#02BFA9"));
        refreshListener = new RefreshListener();
        refreshLayout.setOnRefreshListener(refreshListener);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        refreshLayout.setProgressViewOffset(false, size.y / 10, size.y / 5);

        //Sorting spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.feed_sort_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setup spinner
        sortSpinner.setAdapter(adapter);
        sortTypeSelectedListener = new OnSortTypeSelectedListener();
        sortSpinner.setOnItemSelectedListener(sortTypeSelectedListener);

        // Setup footer icons
        favouritesButton.setColorFilter(getResources().getColor(R.color.peek_grey));
        myUniButton.setColorFilter(getResources().getColor(R.color.peek_grey));
        worldButton.setColorFilter(getResources().getColor(R.color.peek_orange_primary));
    }

    private void initializeComponents() {
        fragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .activityModule(getActivityModule())
                .build();
        fragmentComponent.inject(this);
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Log.d("FeedFragment", "Options height: " + optionsBarHolder.getMeasuredHeight());
            int verticalOffset = recyclerView.computeVerticalScrollOffset();

            Log.d("FeedFragment", "dy: " + verticalOffset + "| Options Y: " + optionsBarHolder.getTranslationY());
            if (verticalOffset < optionsBarHolder.getMeasuredHeight()
                    || verticalOffset < optionsBarHolder.getHeight()) {
                optionsBarHolder.setTranslationY(-verticalOffset);
            }
        }
    }
    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
            @Override
            public void onRefresh() {
                feedPresenter.updateFeed();
            }

    }

    // --------------- Data related stuff --------------------


    @Override
    public void setSelectorAreas(List<String> areas) {
        locationSelectorAdapter.setAreasFunnel(areas);
        notifySelectorChanged();
    }

    private void notifySelectorChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationSelectorAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateFeed(List<University> universities) {
        if (cardAdapter != null) {
            if (universities != null) {
                cardAdapter.setUniversityList(universities);
                notifyCardsChanged();
            }
            if (refreshLayout.isRefreshing())   refreshLayout.setRefreshing(false);
        }
    }

    private void notifyCardsChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardAdapter.notifyDataSetChanged();
            }
        });
    }

    //------------- SORTING --------------------

    private class OnSortTypeSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            onRequestFeedSort(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void onRequestFeedSort(int criteria) {
        feedPresenter.sortFeedBy(criteria);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Other UI stuff


    private int getStatusBarHeight() {
        //Set up height of status bar background (empty recycler view)
        int statusBarHeight = 0;
        int resourceId = getResources().
                getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}

