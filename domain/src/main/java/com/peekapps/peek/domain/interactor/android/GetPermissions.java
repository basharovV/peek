/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.domain.interactor.android;

import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.interactor.Interactor;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Slav on 02/03/2016.
 */
public class GetPermissions extends Interactor {

    public GetPermissions(ThreadExecutor threadExecutor, PostExecutionThread uiThread) {
        super(threadExecutor, uiThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    @Override
    public void execute(Subscriber UseCaseSubscriber) {
        super.execute(UseCaseSubscriber);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

}
