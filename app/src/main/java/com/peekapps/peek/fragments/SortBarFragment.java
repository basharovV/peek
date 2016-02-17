package com.peekapps.peek.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peekapps.peek.R;
import com.peekapps.peek.views.SortButtonView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Slav on 26/07/2015.
 */
public class SortBarFragment extends Fragment {

    private static final int NUMBER_OF_BUTTONS = 4;

    int mode = FeedFragment.SORT_TYPE_POPULARITY; //Default mode

    private SortButtonView trendingButton;
    private SortButtonView clockButton;
    private SortButtonView pinButton;
    private SortButtonView friendsButton;

    private SortButtonClickListener sortButtonClickListener;

    private SparseArray<SortButtonView> sortButtons;

    private OnChangeSortListener changeSortListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sort_bar, container, false);
        trendingButton = (SortButtonView) rootView.findViewById(R.id.trendingSortButton);
        clockButton = (SortButtonView) rootView.findViewById(R.id.clockSortButton);
        pinButton = (SortButtonView) rootView.findViewById(R.id.locationSortButton);
        friendsButton = (SortButtonView) rootView.findViewById(R.id.friendsSortButton);

        //Add buttons to array
        sortButtons = new SparseArray<>(NUMBER_OF_BUTTONS);
        sortButtons.put(FeedFragment.SORT_TYPE_POPULARITY, trendingButton);
        sortButtons.put(FeedFragment.SORT_TYPE_LAST_UPDATE, clockButton);
        sortButtons.put(FeedFragment.SORT_TYPE_DISTANCE, pinButton);
        sortButtons.put(FeedFragment.SORT_TYPE_FRIENDS, friendsButton);

        sortButtonClickListener = new SortButtonClickListener();

        setUpButtons();
        return rootView;

    }

    private void setUpButtons() {

        for (int i = 0; i < sortButtons.size(); i++) {
            if (i > 0) {
                sortButtons.get(i).setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.peek_inactive));
            }
            sortButtons.get(i).setAction(i);
            sortButtons.get(i).setOnClickListener(sortButtonClickListener);
        }

    }

    public void setChangeSortListener(OnChangeSortListener changeSortListener) {
        this.changeSortListener = changeSortListener;
    }

    public class SortButtonClickListener implements View.OnClickListener {

        public SortButtonClickListener() {
        }
        @Override
        public void onClick(View v) {
            Log.d("SortButton", "CLICKED!!");
            SortButtonView clickedButton = (SortButtonView) v;
            changeSortListener.onRequestFeedSort(clickedButton.getAction());
            mode = clickedButton.getAction();
            setSelectedButton(clickedButton, mode);
        }
    }

    private void setSelectedButton(SortButtonView v, int mode) {
        sortButtons.get(mode).setColorFilter(null);
        for (int i = 0; i < sortButtons.size(); i++) {
            if (sortButtons.get(i).getAction() != mode) {
                sortButtons.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.peek_inactive));
            }
        }
    }

}