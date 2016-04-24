/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.BaseFragment;
import com.peekapps.peek.presentation.ui.login.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Slav on 20/04/2016.
 */
public class TutorialFragment extends BaseFragment {

    @Bind(R.id.tutorialFragmentImage)       ImageView tutorialImage;
    @Bind(R.id.tutorialFragmentText)        TextView tutorialText;
    @Bind(R.id.tutorialFragmentDoneButton)  TextView tutorialDoneButton;

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
        if (args.getBoolean("last")) {
            tutorialDoneButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tutorialFragmentDoneButton)
    protected void gotToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }
}
