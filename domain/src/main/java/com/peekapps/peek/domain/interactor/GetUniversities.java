/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.interactor;

import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.repository.UniRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Slav on 10/03/2016.
 */
public class GetUniversities extends Interactor{

    private final UniRepository uniRepository;

    @Inject
    public GetUniversities(UniRepository uniRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.uniRepository = uniRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return uniRepository.universities();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }
}
