/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;


/**
 * Created by Slav on 12/11/2015.
 */
public class TextFocusFragment extends Fragment {

    private TextView itemGrey;
    private TextView itemOrange;
    private ImageView itemBg;
    private TextView dummyItem;
    private ImageView rightConnector, leftConnector;
    private String areaText = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_focus, container, false);

        itemOrange = (TextView) rootView.findViewById(R.id.textFocusItemOrange);
        itemGrey = (TextView) rootView.findViewById(R.id.textFocusItemGrey);
        dummyItem = (TextView) rootView.findViewById(R.id.textFocusItemDummy);

        //Two views for crossfading colours
        itemOrange.setText(areaText);
        itemGrey.setText(areaText);
        //Set the size of parent view
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
        if (itemOrange != null && dummyItem != null) {
            itemOrange.setText(text);
            itemGrey.setText(text);
            dummyItem.setText(text);
        }
    }

    public int getTextLength(){
        return areaText.length();
    }

    public void setTextSize(float size) {
        itemOrange.setTextSize(size);
        itemGrey.setTextSize(size);
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
