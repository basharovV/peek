/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.uniprofile;

import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.DaggerUniComponent;
import com.peekapps.peek.presentation.common.di.components.UniComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.UniModule;
import com.peekapps.peek.presentation.model.PhotoModel;
import com.peekapps.peek.presentation.ui.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Slav on 23/05/2016.
 */
public class UniProfileActivity extends BaseActivity implements UniProfileView,
        AppBarLayout.OnOffsetChangedListener{

    @Bind(R.id.uniProfileRecyclerView)
    RecyclerView thumbnailView;

    @Bind(R.id.uniProfileToolbarTitle)
    TextView toolbarTitle;

    @Bind(R.id.uniProfileAppBar) AppBarLayout appBarLayout;
    @Bind(R.id.uniProfileBackButton)
    ImageButton backButton;

    @Bind(R.id.uniProfileBackButtonDark)
    ImageButton backButtonDark;

    private GridLayoutManager gridLayout;

    @Inject UniProfileThumbnailAdapter thumbnailAdapter;

    @Inject UniProfilePresenter uniProfilePresenter;

    UniComponent uniComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uni_profile_activity);
        ButterKnife.bind(this);
        initializeComponent();
        initializeActivity();
        initialize();
    }

    private void initializeComponent() {
        uniComponent = DaggerUniComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .uniModule(new UniModule())
                .build();
        uniComponent.inject(this);
    }


    private void initializeActivity() {
        uniProfilePresenter.setUniProfileView(this);
        backButtonDark.setAlpha(0f);
        ColorFilter colorFilter = new ColorFilter();
        backButtonDark.setColorFilter(getResources().getColor(R.color.peek_dark));
        appBarLayout.addOnOffsetChangedListener(this);
        toolbarTitle.setAlpha(0f);
        gridLayout = new GridLayoutManager(this, 3);
        thumbnailView.setLayoutManager(gridLayout);
        thumbnailView.setAdapter(thumbnailAdapter);
    }

    private void initialize() {
        uniProfilePresenter.initialize();
    }


    // ------- UI RELATED -----------

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentScrolled = (float) Math.abs(verticalOffset) / (float) maxScroll;
        toolbarTitle.setAlpha(percentScrolled);
        backButton.setAlpha(1 - percentScrolled);
        backButtonDark.setAlpha(percentScrolled);
    }


    // ------- DATA RELATED ----------


    @Override
    public void setThumbPhotos(List<PhotoModel> photoList) {
        thumbnailAdapter.setPhotoList(photoList);
    }
}
