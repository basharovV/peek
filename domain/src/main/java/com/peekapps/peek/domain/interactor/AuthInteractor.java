package com.peekapps.peek.domain.interactor;

import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Slav on 22/02/2016.
 */
public class AuthInteractor extends Interactor {

    public static final int ACTION_LOGIN = 0;
    public static final int ACTION_LOGOUT = 1;

    @Inject
    public AuthInteractor(ThreadExecutor threadExecutor, PostExecutionThread uiThread) {
        super(threadExecutor, uiThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }
}
