package com.peekapp.example.peek.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Slav on 17/08/2015.
 */
public class SortButtonView extends ImageView implements View.OnTouchListener {

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
}
