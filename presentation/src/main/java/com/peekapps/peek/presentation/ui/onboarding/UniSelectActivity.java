/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.ActivityComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerActivityComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerUniComponent;
import com.peekapps.peek.presentation.common.di.components.UniComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.UniModule;
import com.peekapps.peek.presentation.ui.BaseActivity;
import com.peekapps.peek.presentation.ui.views.CenterViewPager;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Slav on 30/04/2016.
 */
public class UniSelectActivity extends BaseActivity implements UniSelectView{

    @Bind(R.id.uniSelectPager)              CenterViewPager uniPager;
    @Bind(R.id.uniSelectCenterUniName)      TextView selectedUniName;
    @Bind(R.id.uniSelectDoneButton)         FrameLayout doneButton;

    @Inject UniSelectPagerAdapter uniSelectPagerAdapter;

    @Inject UniSelectPresenter uniSelectPresenter;

    ActivityComponent activityComponent;
    UniComponent uniComponent;

    // Data
    List<University> suggestedUnis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uni_selection_activity);
        ButterKnife.bind(this);
        initializeComponents();
        getUniComponent().inject(this);
        initializeActivity();
        initialize();
    }

    private void initializeComponents() {
        uniComponent = DaggerUniComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .uniModule(new UniModule())
                .build();
    }

    public UniComponent getUniComponent() {
        return uniComponent;
    }


    private void initializeActivity() {
        uniPager.setAdapter(uniSelectPagerAdapter);
        uniPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (suggestedUnis != null) {
                    if (suggestedUnis.size() > position)
                        selectedUniName.setText(suggestedUnis.get(position).getName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Color -> greyscale scrolling animation
        uniPager.setPageTransformer(false, new PagerSaturationTransformer());
    }

    private void initialize() {
        uniSelectPresenter.setUniSelectView(this);
        uniSelectPresenter.initialize();
    }

    // UI related

    private class PagerSaturationTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            ImageView uniLogoImage = (ImageView) page.findViewById(R.id.uniSelectPagerFragmentImage);

            if (Math.abs(position) < 0.5f) {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(1 - (Math.abs(position) * 2));
                ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                uniLogoImage.setColorFilter(colorFilter);
                uniLogoImage.setAlpha(1 - (Math.abs(position) * 2));
            }
        }
    }

    @OnClick(R.id.uniSelectDoneButton)
    public void onDoneButtonClick() {
        getNavigator().navigateToMain(this);
    }

    // Data related
    @Override
    public void setSuggestedUnis(List<University> suggestedUnis) {
        this.suggestedUnis = suggestedUnis;
        selectedUniName.setText(suggestedUnis.get(uniPager.getCurrentItem()).getName());
        uniSelectPagerAdapter.setUniversities(suggestedUnis);
    }

    @Override
    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
