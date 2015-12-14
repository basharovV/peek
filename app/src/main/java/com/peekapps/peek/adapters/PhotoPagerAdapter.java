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
import com.peekapps.peek.views.OnAreaSelectorReadyListener;
import com.peekapps.peek.views.OnPhotoPagerReadyListener;

import java.util.ArrayList;

/**
 * Created by Slav on 30/11/2015.
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
    private int[] imageResources;

    private static final int NUMBER_OF_FRAGMENTS = 4;
    private OnPhotoPagerReadyListener listener;
    SparseArray<PhotoFragment> registeredFragments = new SparseArray<PhotoFragment>();

    public PhotoPagerAdapter(FragmentManager fm) {
        super(fm);

        imageResources = new int[] {
            R.drawable.rockerfeller1,
            R.drawable.rockerfeller2,
            R.drawable.rockerfeller3,
            R.drawable.rockerfeller4 };
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public Fragment getItem(int position) {
        return new PhotoFragment();
    }

//    @Override
//    public float getPageWidth(int position) {
////        if (position == 0 || position == getCount() - 1) {
////            return 0.3f;
////        }
//        return 0.4f;
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoFragment fragment = (PhotoFragment) super.instantiateItem(container, position);
        Bundle photoArg = new Bundle();
        photoArg.putInt("resource", imageResources[position]);
        fragment.setArguments(photoArg);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

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