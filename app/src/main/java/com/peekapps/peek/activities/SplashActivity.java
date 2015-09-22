package com.peekapps.peek.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import java.lang.ref.WeakReference;


public class SplashActivity extends Activity {


    private  int SPLASH_TIME_OUT = 3000;
    private final SplashHandler mHandler = new SplashHandler(this);
    private final Runnable splashRunnable = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }
    };

    private static class SplashHandler extends Handler {
        private final WeakReference<SplashActivity> splashActivity;

        public SplashHandler(SplashActivity activity) {
            splashActivity = new WeakReference<SplashActivity>(activity);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(splashRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(splashRunnable);
    }
}
