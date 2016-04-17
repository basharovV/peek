/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Slav on 07/03/2016.
 */
@Singleton
public class UIEventBus<T> {

    private final Subject<T, T> subject;

    @Inject
    public UIEventBus() {
        this(PublishSubject.<T>create());
    }

    public UIEventBus(Subject<T, T> subject) {
        this.subject = subject;
    }

    public <E extends T> void postEvent(E event) {
        subject.onNext(event);
    }

    public Observable<T> toObservable() {
        return subject;
    }

    public <E extends T> Observable<E> observeEvents(Class<E> eventClass) {
        return subject.ofType(eventClass)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()); // pass only events of specified type, filter all other
    }
}
