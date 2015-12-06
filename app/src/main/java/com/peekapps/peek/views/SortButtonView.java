package com.peekapps.peek.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.peekapps.peek.fragments.OnChangeSortListener;

/**
 * Created by Slav on 17/08/2015.
 */
public class SortButtonView extends ImageView implements View.OnTouchListener {

    private int action;
    private OnChangeSortListener listener;

    public SortButtonView(Context context) {
        super(context);
    }

    public SortButtonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(0xFFFFFFFF);
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(Color.TRANSPARENT);
        }
        return false;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
