/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by Slav on 30/11/2015.
 */
public class TextFocusPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUMBER_OF_FRAGMENTS = 5;
    private List<String> areaNames;
    private OnAreaSelectorReadyListener listener;
    SparseArray<TextFocusFragment> registeredFragments = new SparseArray<TextFocusFragment>();

    @Inject
    public TextFocusPagerAdapter(@Named("childFragmentManager") FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment textFocusFragment = new TextFocusFragment();
        Bundle args = new Bundle();
        if (areaNames.get(position) != null) {
            args.putString("areaName", areaNames.get(position));
        }
        else args.putString("areaName", "");
        textFocusFragment.setArguments(args);
        return textFocusFragment;
    }

    @Override
    public float getPageWidth(int position) {
//        TextFocusFragment focusFragment = ((TextFocusFragment) getRegisteredFragment(position));
//        if (focusFragment != null) {
//            float nChars = (float) areaNames[position].length();
//            if (nChars == 0) {
//                nChars = 1;
//            }
//            else if (nChars > 18) return 1.0f;
//            float width = 0.5f + (0.5f - (1.0f / (nChars)));
//            Log.d("TextFocusAdapter", "Width of position " + position + ": " + width + " (" + nChars + " chars)");
//            return width;
//        }

        return super.getPageWidth(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextFocusFragment fragment = (TextFocusFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        boolean allFragmentsReady = true;
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
            if (registeredFragments.get(i) == null) {
                allFragmentsReady = false;
            }
        }
        if (allFragmentsReady) {

        }
        return fragment;
    }

    public void setAreasFunnel(List<String> areas) {
        clearAreas();
        this.areaNames = areas;
    }

    public void clearAreas() {
        this.registeredFragments.clear();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public void setFragmentText(int position, String text) {
        TextFocusFragment textFragment = ((TextFocusFragment) getRegisteredFragment(position));
        if (textFragment != null) textFragment.setText(text);
        notifyDataSetChanged();
    }

    public void setFragmentTextSize(int position, float size) {
        TextFocusFragment textFragment = ((TextFocusFragment) getRegisteredFragment(position));
        if (textFragment != null) textFragment.setTextSize(size);
        notifyDataSetChanged();
    }

}