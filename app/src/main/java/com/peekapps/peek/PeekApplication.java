package com.peekapps.peek;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.test.ApplicationTestCase;

import com.squareup.leakcanary.LeakCanary;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PeekApplication extends MultiDexApplication {

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        LeakCanary.install(this);
    }
}