/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.common.di.modules;

import com.peekapps.peek.domain.executor.PostExecutionThread;
import com.peekapps.peek.domain.executor.ThreadExecutor;
import com.peekapps.peek.domain.interactor.GetSuggestedUniversities;
import com.peekapps.peek.domain.interactor.GetUniversities;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.domain.repository.UniRepository;
import com.peekapps.peek.domain.repository.UserRepository;
import com.peekapps.peek.domain.services.LocationProvider;
import com.peekapps.peek.presentation.common.di.PerActivity;
import com.peekapps.peek.presentation.common.di.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Slav on 10/03/2016.
 */
@Module
public class UniModule {

    @Provides @PerFragment @Named("universities")
    Interactor getUniversitiesUseCase(UniRepository uniRepository, ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread) {
        return new GetUniversities(uniRepository, threadExecutor, postExecutionThread);
    }

    @Provides @PerActivity @Named("suggestedUniversities")
    Interactor getSuggestedUnisUseCase(UniRepository uniRepository,
                                       LocationProvider locationProvider,
                                       ThreadExecutor threadExecutor,
                                       PostExecutionThread postExecutionThread) {
        return new GetSuggestedUniversities(uniRepository, locationProvider, threadExecutor, postExecutionThread);
    }
}
