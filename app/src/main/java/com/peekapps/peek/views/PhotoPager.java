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

    private boolean isLocked;
    private static final int OFFSCREEN_LIMIT = 2;

    public PhotoPager(Context context) {
        super(context);
        isLocked = false;
    }

    public PhotoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPageTransformer(false, new PhotoPageTransformer());
        this.setOffscreenPageLimit(OFFSCREEN_LIMIT);
        isLocked = false;
//        this.addOnPageChangeListener(new PhotoPageListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isLocked && super.onTouchEvent(event);
    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
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
