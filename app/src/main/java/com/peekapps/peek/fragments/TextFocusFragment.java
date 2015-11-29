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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_focus, container, false);

        item = (TextView) rootView.findViewById(R.id.textFocusItem);

        return rootView;
    }

    public void setText(String text) {
        item.setText(text);
    }

    public void setTextSize(float size) {
        item.setTextSize(size);
    }

}
