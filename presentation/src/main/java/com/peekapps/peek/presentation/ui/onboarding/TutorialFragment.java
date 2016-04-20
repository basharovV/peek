/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Slav on 20/04/2016.
 */
public class TutorialFragment extends Fragment {

    @Bind(R.id.tutorialFragmentImage)       ImageView tutorialImage;
    @Bind(R.id.tutorialFragmentText)        TextView tutorialText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tutorial_fragment, container, false);
        ButterKnife.bind(this, rootView);
        initializeFragment();
        return rootView;
    }

    private void initializeFragment() {
        Bundle args = getArguments();
        tutorialImage.setImageResource(args.getInt("image"));
        tutorialText.setText(getResources().getString(args.getInt("text")));
    }
}
