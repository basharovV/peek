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

import javax.inject.Inject;
import javax.inject.Named;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 3;
    /**
     * Set the text entries and the images below. These are static resources.
     */
    private final int[] tutorialTextList = new int[] {
            R.string.tutorial_text_1,
            R.string.tutorial_text_2,
            R.string.tutorial_text_3
    };

    private final int[] tutorialImageList = new int[] {
            R.drawable.tutorial_fragment_1,
            R.drawable.tutorial_fragment_2,
            R.drawable.tutorial_fragment_3,
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
        args.putBoolean("last", position == (COUNT - 1));
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
