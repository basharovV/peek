package com.peekapps.peek.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.peekapps.peek.R;
import com.peekapps.peek.fragments.PhotoFragment;
import com.peekapps.peek.fragments.TextFocusFragment;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.views.OnAreaSelectorReadyListener;
import com.peekapps.peek.views.OnPhotoPagerReadyListener;

import java.util.ArrayList;

/**
 * Created by Slav on 30/11/2015.
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
    //Test purposes
    private int[] imageResources = new int[] {
            R.drawable.rockerfeller1,
            R.drawable.rockerfeller2,
            R.drawable.rockerfeller3,
            R.drawable.rockerfeller4 };

    //The place selected (from map or feed)
    private Place selectedPlace;

    // Total number of photos for this place
    private static final int PHOTO_COUNT = 4;

    // Current photo being shown in pager
    private int currentIndex = 0;

    private OnPhotoPagerReadyListener listener;
    SparseArray<PhotoFragment> registeredFragments = new SparseArray<PhotoFragment>();

    public PhotoPagerAdapter(FragmentManager fm) {
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

    public void setPlace(Place pl) {
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

    public void setOnReadyListener(OnPhotoPagerReadyListener listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        listener.onPhotoPagerReady();
    }

//    public void setFragmentImage(int position, int resource) {
//        if (getRegisteredFragment(position) != null) {
//            ((PhotoFragment) getRegisteredFragment(position)).setImage(resource);
//        }
//    }
}