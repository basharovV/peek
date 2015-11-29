package com.peekapps.peek.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.fragments.TextFocusFragment;

/**
 * Created by Slav on 12/11/2015.
 */
public class TextFocusViewPager extends ViewPager {

    private Activity activity;

    private TextFocusPagerAdapter pagerAdapter;

    public TextFocusViewPager(Context context) {
        super(context);
    }

    public void initialize(Activity activity) {
        this.activity = activity;
        pagerAdapter = new TextFocusPagerAdapter(((PeekViewPager) activity)
                .getSupportFragmentManager());
        this.setAdapter(pagerAdapter);
        this.addOnPageChangeListener(new TextFocusPageListener());
    }

    /**
     * Listener to change the text size depending on user scroll action
     *
     */
    public class TextFocusPageListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public class TextFocusPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public TextFocusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TextFocusFragment();
                case 1:
                    return new TextFocusFragment();
                case 2:
                    return new TextFocusFragment();
                case 3:
                    return new TextFocusFragment();

            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
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

        public void setItemText(int position, String text) {
            TextFocusFragment textFragment = ((TextFocusFragment) getRegisteredFragment(position));
            if (textFragment != null) textFragment.setText(text);
        }
    }
}
