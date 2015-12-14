package com.peekapps.peek.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.R;

/**
 * Created by Slav on 09/12/2015.
 */
public class PhotoPager extends ViewPager {

    public PhotoPager(Context context) {
        super(context);
    }

    public PhotoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPageTransformer(false, new PhotoPageTransformer());
        this.setOffscreenPageLimit(4);
//        this.addOnPageChangeListener(new PhotoPageListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class PhotoPageTransformer implements PageTransformer {

        public void transformPage(View page, float position) {
            ImageView photoView;
            int pageWidth = page.getWidth();
            if (page.findViewById(R.id.photoPagerPhoto) != null) {
                photoView = (ImageView) page.findViewById(R.id.photoPagerPhoto);
                photoView.setTranslationX(-position * (pageWidth / 2));
            }
        }
    }
}
