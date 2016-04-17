/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.media;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.model.PlaceModel;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by Slav on 30/11/2015.
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
    //Test purposes
    private int[] imageResources = new int[] {
            R.drawable.universityofedinburgh_1,
            R.drawable.universityofedinburgh_2,
            R.drawable.universityofedinburgh_3
    };

    //The place selected (from map or feed)
    private PlaceModel selectedPlace;

    // Total number of photos for this place
    private static final int PHOTO_COUNT = 3;

    // Current photo being shown in pager
    private int currentIndex = 0;

//    private OnPhotoPagerReadyListener listener;
    SparseArray<PhotoFragment> registeredFragments = new SparseArray<PhotoFragment>();

    @Inject
    public PhotoPagerAdapter(@Named("childFragmentManager") FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PHOTO_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle photoArg = new Bundle();
        photoArg.putInt("res", getPhoto(position));
        Fragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(photoArg);
        return photoFragment;
    }

    public void setPlace(PlaceModel pl) {
        selectedPlace = pl;

    }

    private int getPhoto(int index) {
        if (currentIndex < imageResources.length) {
            return imageResources[index];
        }
        currentIndex = index;
        return 0;
    }

//    @Override
//    public float getPageWidth(int position) {
////        if (position == 0 || position == getCount() - 1) {
////            return 0.3f;
////        }
//        return 0.4f;
//    }


    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

//    public void setFragmentImage(int position, int resource) {
//        if (getRegisteredFragment(position) != null) {
//            ((PhotoFragment) getRegisteredFragment(position)).setImage(resource);
//        }
//    }
}