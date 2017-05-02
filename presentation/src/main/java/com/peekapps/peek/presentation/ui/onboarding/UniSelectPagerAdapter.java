/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.ui.feed.OnAreaSelectorReadyListener;
import com.peekapps.peek.presentation.ui.feed.TextFocusFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Fragment
 */
public class UniSelectPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUMBER_OF_FRAGMENTS = 5;

    /*
    The URL's of the University logos
     */
    private List<String> imageUrls;
    private List<University> suggestedUnis;

    private OnAreaSelectorReadyListener listener;
    SparseArray<UniSelectPagerFragment> registeredFragments = new SparseArray<>();

    @Inject
    public UniSelectPagerAdapter(@Named("supportFragmentManager") FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment uniLogoFragment = new UniSelectPagerFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", "https://upload.wikimedia.org/wikipedia/en/5/5d/SUFClogotrans.png");
//        if (imageUrls.get(position) != null) {
//            args.putString("imageUrl", "https://upload.wikimedia.org/wikipedia/en/5/5d/SUFClogotrans.png");
//        }
//        else {
//            args.putString("imageUrl", "https://upload.wikimedia.org/wikipedia/en/5/5d/SUFClogotrans.png");
//        }
        uniLogoFragment.setArguments(args);
        return uniLogoFragment;
    }

    @Override
    public float getPageWidth(int position) {

        return super.getPageWidth(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        UniSelectPagerFragment uniLogoFragment = (UniSelectPagerFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, uniLogoFragment);
        boolean allFragmentsReady = true;
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
            if (registeredFragments.get(i) == null) {
                allFragmentsReady = false;
            }
        }
        if (allFragmentsReady) {

        }
        return uniLogoFragment;
    }

    public void setImageUrls(List<String> imageUrls) {
        clearImageUrls();
        this.imageUrls = imageUrls;
    }

    public void setUniversities(List<University> suggestedUnis) {
        this.suggestedUnis = suggestedUnis;
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
            if (registeredFragments.get(i) != null) {
                registeredFragments.get(i).loadDrawable(suggestedUnis.get(i).getId());
            }
        }
    }

    public void clearImageUrls() {
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



//    public void setFragmentText(int position, String text) {
//        TextFocusFragment textFragment = ((TextFocusFragment) getRegisteredFragment(position));
//        if (textFragment != null) textFragment.setText(text);
//        notifyDataSetChanged();
//    }
//
//    public void setFragmentTextSize(int position, float size) {
//        TextFocusFragment textFragment = ((TextFocusFragment) getRegisteredFragment(position));
//        if (textFragment != null) textFragment.setTextSize(size);
//        notifyDataSetChanged();
//    }

}