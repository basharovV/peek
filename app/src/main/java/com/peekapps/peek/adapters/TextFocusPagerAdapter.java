package com.peekapps.peek.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.peekapps.peek.R;
import com.peekapps.peek.fragments.TextFocusFragment;
import com.peekapps.peek.views.OnAreaSelectorReadyListener;

import java.util.ArrayList;

/**
 * Created by Slav on 30/11/2015.
 */
public class TextFocusPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUMBER_OF_FRAGMENTS = 4;
    private String[] areaNames;
    private OnAreaSelectorReadyListener listener;
    SparseArray<TextFocusFragment> registeredFragments = new SparseArray<TextFocusFragment>();


    public TextFocusPagerAdapter(FragmentManager fm) {
        super(fm);
        areaNames = new String[NUMBER_OF_FRAGMENTS];
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public Fragment getItem(int position) {
        return new TextFocusFragment();
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
        TextFocusFragment fragment = (TextFocusFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        boolean allFragmentsReady = true;
        for (int i = 0; i < NUMBER_OF_FRAGMENTS; i++) {
            if (registeredFragments.get(i) == null) {
                allFragmentsReady = false;
            }
        }
        if (allFragmentsReady) notifyListener();
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

    public void setOnReadyListener(OnAreaSelectorReadyListener listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        listener.onSelectorReady();
    }

    public void setText(int position, String text) {
        areaNames[position] = text;
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