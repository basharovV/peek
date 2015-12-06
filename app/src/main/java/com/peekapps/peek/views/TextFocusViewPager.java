package com.peekapps.peek.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peekapps.peek.R;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.adapters.TextFocusPagerAdapter;
import com.peekapps.peek.fragments.TextFocusFragment;

/**
 * Created by Slav on 12/11/2015.
 */
public class TextFocusViewPager extends ViewPager {

    private String TAG = "TFViewPager";
    private Activity activity;

    private int currentPage = 0;

    private TextFocusPagerAdapter pagerAdapter;

    public TextFocusViewPager(Context context) {
        super(context);
    }

    public TextFocusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPageTransformer(false, new TextFocusPageTransformer());
        this.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.area_selector_pager_margin));
        this.setOffscreenPageLimit(4);
        this.addOnPageChangeListener(new TextFocusPageListener());
//        this.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.area_selector_pager_margin));
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        this.pagerAdapter = (TextFocusPagerAdapter) adapter;
    }

    @Override
    public TextFocusPagerAdapter getAdapter() {
        return pagerAdapter;
    }

    public void initialize(Activity activity) {
        this.activity = activity;
    }


    private class TextFocusPageTransformer implements ViewPager.PageTransformer {
        private static final float NORMAL_SIZE = 18;
        private static final float CENTER_SIZE = 26;
        private static final float SIZE_DIFF = 12;

        @Override
        public void transformPage(View page, float position) {
            TextView areaText;
            if (page.findViewById(R.id.textFocusItem) != null) {
                areaText = (TextView) page.findViewById(R.id.textFocusItem);
                //This is the left page not currently visible
                if (position < -1) { // [-Infinity,-1)
                    areaText.setTextSize(NORMAL_SIZE);

                } else if (position <= 1) { // [-1,1]

                    areaText.setTextSize(CENTER_SIZE - (Math.abs(SIZE_DIFF * (position))));
                    areaText.setAlpha(1 - (Math.abs(position) / 3));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    areaText.setTextSize(NORMAL_SIZE);
                }
            }
        }
    }

    /**
     * Listener to change the text size depending on user scroll action
     */
    public class TextFocusPageListener implements OnPageChangeListener {
        private static final float NORMAL_SIZE = 18;
        private static final float CENTER_SIZE = 24;
        private static final float SIZE_DIFF = 6;

        private float previousOffset = 2;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


        }
        //OLD stuff
//                //If moving to the LEFT...
//                if (previousOffset == 2) {
//                    previousOffset = positionOffset;
//                }
//                if (previousOffset > positionOffset) {
//                    // If : [CENTER 0.0 <---- 1.0] towards center - increase size
//                    //  Log.d(TAG, "positionOffset: " + positionOffset);
//                    //Focus
//                    getAdapter().setFragmentTextSize(position, NORMAL_SIZE + (SIZE_DIFF * (positionOffset)));
//                    getAdapter().setFragmentTextSize(position + 1, NORMAL_SIZE + (SIZE_DIFF * (1.0f - positionOffset)));
//                }
//                //If moving to the RIGHT...
//                else if (previousOffset < positionOffset) {
//                    // If : [ 0.0 ----> CENTER 1.0 ] towards center - increase size
//                    //Focus next
//                    getAdapter().setFragmentTextSize(position + 1, NORMAL_SIZE + (SIZE_DIFF * positionOffset));
//                    //Defocus previoius
//                    getAdapter().setFragmentTextSize(position, NORMAL_SIZE + (SIZE_DIFF * (1.0f - positionOffset)));
//
//                }
//                previousOffset = positionOffset;
//
//            Log.d(TAG, "positionOffset: " + positionOffset);
//            Log.d(TAG, "Current page: " + currentPage);
//            Log.d(TAG, "Current position: " + position);


//            switch (currentPage) {
//                //Left-end fragment
//                case 0:
//                    //If not been initialised
//                    if (previousOffset == 2) {
//                        previousOffset = positionOffset;
//                    }
//                    getAdapter().setFragmentTextSize(0, NORMAL_SIZE + (SIZE_DIFF * positionOffset));
//                    previousOffset = positionOffset;
////                    toolbarGroup.setY(-toolbarHeight * (positionOffset));
////                    toolbarGroup.setAlpha(1 - positionOffset);
//                    break;
//                //Middle fragment
//                case 1:
//                    //If not been initialised
//                    if (previousOffset == 2) {
//                        previousOffset = positionOffset;
//                    }
//                    //...moving to the LEFT
//                    if (previousOffset > positionOffset) {
//                        //towards LEFT FRAGMENT
//                        if (position == 0) {
//                            getAdapter().setFragmentTextSize(0, NORMAL_SIZE + (SIZE_DIFF * positionOffset));
////                            toolbarGroup.setAlpha(1 - positionOffset);
////                            toolbarGroup.setTranslationY(-toolbarHeight * (positionOffset));
//                        }
//                        //towards THIS FRAGMENT FROM THE RIGHT
//                        else {
//                            getAdapter().setFragmentTextSize(0, NORMAL_SIZE + (SIZE_DIFF * (1 - positionOffset)));
////                            toolbarGroup.setAlpha(positionOffset);
////                            toolbarGroup.setTranslationY(-toolbarHeight * (1.0f - positionOffset));
//                        }
//                    }
//                    //...moving to the RIGHT
//                    else if (previousOffset < positionOffset) {
//                        //towards FEED
//                        if (position == 1) {
//                            toolbarGroup.setAlpha(positionOffset);
//                            toolbarGroup.setTranslationY(-toolbarHeight * (1.0f - positionOffset));
//                        }
//                        //towards CAMERA
//                        else {
////                            toolbarGroup.setAlpha(1.0f - positionOffset);
////                            toolbarGroup.setTranslationY(-toolbarHeight * positionOffset);
//                        }
//                    }
//                    previousOffset = positionOffset;
//                    break;
//                case 2:
//                    if (positionOffset == 0) toolbarGroup.setAlpha(1.0f);
//                    else {
//                        toolbarGroup.setAlpha(positionOffset);
//                        toolbarGroup.setTranslationY(-toolbarHeight * (1 - positionOffset));
//                    }
//                    break;
//            }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
//            getAdapter().setFragmentTextSize(position, NORMAL_SIZE + (SIZE_DIFF));
        }

        @Override
        public void onPageScrollStateChanged(int scrollState) {
            // A small hack to remove the HW layer that the viewpager add to each page when scrolling.
            if (scrollState != ViewPager.SCROLL_STATE_IDLE) {
                final int childCount = TextFocusViewPager.this.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    TextFocusViewPager.this.getChildAt(i).setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }
        }
    }
}
