package com.peekapps.peek.fragments;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.R;

import org.w3c.dom.Text;

/**
 * Created by Slav on 12/11/2015.
 */
public class TextFocusFragment extends Fragment {

    private TextView item;
    private ImageView itemBg;
    private TextView dummyItem;
    private ImageView rightConnector, leftConnector;
    private String areaText = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_focus, container, false);

        item = (TextView) rootView.findViewById(R.id.textFocusItem);
        dummyItem = (TextView) rootView.findViewById(R.id.textFocusItemDummy);
        item.setText(areaText);
        dummyItem.setText(areaText);
        itemBg = (ImageView) rootView.findViewById(R.id.textFocusBackground);
//        rightConnector = (ImageView) rootView.findViewById(R.id.locToolbarConnectorR);
//        leftConnector = (ImageView) rootView.findViewById(R.id.locToolbarConnectorL);
//        leftConnector.setScaleType(ImageView.ScaleType.MATRIX);
//        setupConnectors();

        return rootView;
    }

    private void setupConnectors() {
        //Connector version
//        Drawable leftDrawable = leftConnector.getDrawable();
//        Rect leftRect = leftDrawable.getBounds();
//
//        Log.d("TextFocusFragment", "LC view width: " + leftConnector.getMeasuredWidth()
//            + ", LC drawable width: " + leftRect.width());
//
//        Matrix leftMatrix = leftConnector.getImageMatrix();
//        leftMatrix.setTranslate(leftRect.width() - leftConnector.getMeasuredWidth(), 0);
//        leftConnector.setImageMatrix(leftMatrix);

    }

    public void setText(String text) {
        areaText = text;
        if (item != null && dummyItem != null) {
            item.setText(text);
            dummyItem.setText(text);
        }
    }

    public int getTextLength(){
        return areaText.length();
    }

    public void setTextSize(float size) {

        item.setTextSize(size);
        dummyItem.setTextSize(size);
    }

//    public void setRightConnectorVisible(boolean visible) {
//        if (visible) rightConnector.setVisibility(View.VISIBLE);
//        else rightConnector.setVisibility(View.INVISIBLE);
//    }
//
//    public void setLeftConnectorVisible(boolean visible) {
//        if (visible) leftConnector.setVisibility(View.VISIBLE);
//        else leftConnector.setVisibility(View.INVISIBLE);
//    }
}
