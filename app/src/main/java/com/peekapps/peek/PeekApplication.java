package com.peekapps.peek;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class PeekApplication extends MultiDexApplication {

    private static String TAG = "PeekApplication";

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        LeakCanary.install(this);initializeApplication();
        Log.d(TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ...Put any application-specific initialization logic here...
    }
}