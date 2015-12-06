package com.peekapps.peek.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peekapps.peek.R;

/**
 * Created by Slav on 12/11/2015.
 */
public class TextFocusFragment extends Fragment {

    private TextView item;
    private String areaText = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_focus, container, false);

        item = (TextView) rootView.findViewById(R.id.textFocusItem);
        item.setText(areaText);

        return rootView;
    }

    public void setText(String text) {
        if (item == null) {
            areaText = text;
        }
        else {
            item.setText(text);
        }
    }

    public void setTextSize(float size) {
        item.setTextSize(size);
    }

}
