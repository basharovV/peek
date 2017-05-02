/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.BaseFragment;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Slav on 07/05/2016.
 */
public class UniSelectPagerFragment extends BaseFragment {

    @Bind(R.id.uniSelectPagerFragmentImage)
    ImageView uniLogo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.uni_select_pager_fragment, container, false);
        ButterKnife.bind(this, rootView);
        loadImage(getArguments().getString("imageUrl"));
        return rootView;
    }

    public void loadImage(String url) {
        Picasso.with(getContext())
                .load(url)
                .into(uniLogo);
    }

    public void loadDrawable(String uniId) {
        int imageId = getResources().getIdentifier(uniId, "drawable", getContext().getPackageName());
        Picasso.with(getContext())
                .load(imageId)
                .into(uniLogo);
    }
}
