/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import android.util.Log;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.domain.interactor.DefaultSubscriber;
import com.peekapps.peek.domain.interactor.GetSuggestedUniversities;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.presentation.Presenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Slav on 06/05/2016.
 */
public class UniSelectPresenter implements Presenter {

    UniSelectView uniSelectView;

    Interactor getSuggestedUnisUseCase;
    OnSuggestedUnisReadySubscriber unisReadySubscriber;

    @Inject
    public UniSelectPresenter(@Named("suggestedUniversities") Interactor getSuggestedUnisUseCase) {
        this.getSuggestedUnisUseCase = getSuggestedUnisUseCase;
    }

    public void setUniSelectView(UniSelectView uniSelectView) {
        this.uniSelectView = uniSelectView;
    }

    public void initialize() {
        unisReadySubscriber = new OnSuggestedUnisReadySubscriber();
        loadSuggestedUnis();
    }



    // Interaction with data layer handled below

    public void loadSuggestedUnis() {
        getSuggestedUnisUseCase.execute(unisReadySubscriber);
    }

    private void showUnisInView(List<University> suggestedUnis) {
        uniSelectView.setSuggestedUnis(suggestedUnis);
    }


    private class OnSuggestedUnisReadySubscriber extends DefaultSubscriber<List<University>> {
        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }

        @Override
        public void onNext(List<University> universityList) {
            for (University uni: universityList) {
                Log.d("UniTest", uni.getName());
            }
            showUnisInView(universityList);
            uniSelectView.toast("Done");
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
