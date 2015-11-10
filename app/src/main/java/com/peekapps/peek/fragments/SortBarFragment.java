package com.peekapps.peek.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peekapps.peek.R;
import com.peekapps.peek.views.SortButtonView;

import java.util.ArrayList;

/**
 * Created by Slav on 26/07/2015.
 */
public class SortBarFragment extends Fragment {

    private Context context;

    private SortButtonView trendingButton;
    private SortButtonView clockButton;
    private SortButtonView pinButton;

    private ArrayList<SortButtonView> sortingButtons;

    private OnChangeSortListener changeSortListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sort_bar, container, false);
        trendingButton = (SortButtonView) rootView.findViewById(R.id.trendingSortButton);
        clockButton = (SortButtonView) rootView.findViewById(R.id.clockSortButton);
        pinButton = (SortButtonView) rootView.findViewById(R.id.locationSortButton);

        setUpButtons();
        return rootView;

    }

    private void setUpButtons() {
        trendingButton.setAction(FeedFragment.FEED_TYPE_POPULARITY);
        clockButton.setAction(FeedFragment.FEED_TYPE_LAST_UPDATE);
        pinButton.setAction(FeedFragment.FEED_TYPE_DISTANCE);
        trendingButton.setSortListener(changeSortListener);
        clockButton.setSortListener(changeSortListener);
        pinButton.setSortListener(changeSortListener);

    }

    public void setChangeSortListener(OnChangeSortListener changeSortListener) {
        this.changeSortListener = changeSortListener;
    }
}