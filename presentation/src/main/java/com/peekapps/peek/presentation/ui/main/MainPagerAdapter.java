package com.peekapps.peek.presentation.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.peekapps.peek.presentation.ui.camera.CameraFragment;
import com.peekapps.peek.presentation.ui.feed.FeedFragment;
import com.peekapps.peek.presentation.ui.map.MapFragment;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Slav on 22/02/2016.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    @Inject
    public MainPagerAdapter(@Named("supportFragmentManager") FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
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
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new FeedFragment();
            default:
                return new CameraFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
