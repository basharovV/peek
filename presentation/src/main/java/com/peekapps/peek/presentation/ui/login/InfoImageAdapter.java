/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Slav on 01/03/2016.
 */
public class InfoImageAdapter extends FragmentStatePagerAdapter {

    @Inject
    public InfoImageAdapter(@Named("supportFragmentManager") FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return getInfoFragment(position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    private InfoImageFragment getInfoFragment(int position) {
        InfoImageFragment fragment = new InfoImageFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }
}
