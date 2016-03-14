package com.peekapps.peek.presentation.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.peekapps.peek.presentation.PeekApplication;
import com.peekapps.peek.presentation.common.di.components.ApplicationComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.navigation.Navigator;

import javax.inject.Inject;

/**
 * Created by Slav on 22/02/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView{

    @Inject
    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link com.peekapps.peek.presentation.common.di.components.ApplicationComponent;}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((PeekApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link com.peekapps.peek.presentation.common.di.modules.ActivityModule;}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public Navigator getNavigator() {
        return navigator;
    }

}
