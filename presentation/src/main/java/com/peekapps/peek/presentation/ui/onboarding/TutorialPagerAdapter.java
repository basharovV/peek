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
import android.view.ViewGroup;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.camera.CameraFragment;
import com.peekapps.peek.presentation.ui.feed.FeedFragment;
import com.peekapps.peek.presentation.ui.map.MapFragment;

import javax.inject.Inject;
import javax.inject.Named;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 4;
    /**
     * Set the text entries and the images below. These are static resources.
     */
    private final int[] tutorialTextList = new int[] {
            R.string.tutorial_text1,
            R.string.tutorial_text2,
            R.string.tutorial_text3,
            R.string.tutorial_text4
    };

    private final int[] tutorialImageList = new int[] {
            R.drawable.tutorial_fragment1,
            R.drawable.tutorial_fragment1,
            R.drawable.tutorial_fragment1,
            R.drawable.tutorial_fragment1,
    };

    @Inject
    public TutorialPagerAdapter(@Named("supportFragmentManager") FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Bundle args = new Bundle();
        args.putInt("text", tutorialTextList[position]);
        args.putInt("image", tutorialImageList[position]);
        fragment.setArguments(args);
//        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
//        registeredFragments.remove(position);
    }


    @Override
    public Fragment getItem(int position) {
        return new TutorialFragment();
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
