package com.peekapp.example.peek.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peekapp.example.peek.R;
import com.peekapp.example.peek.views.SortButtonView;

/**
 * Created by Slav on 26/07/2015.
 */
public class SortBarFragment extends Fragment {

    private SortButtonView trendingButton;
    private SortButtonView clockButton;
    private SortButtonView pinButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sort_bar, container, false);
        trendingButton = (SortButtonView) rootView.findViewById(R.id.trendingSortButton);
        clockButton = (SortButtonView) rootView.findViewById(R.id.clockSortButton);
        pinButton = (SortButtonView) rootView.findViewById(R.id.locationSortButton);

        return rootView;

    }
}