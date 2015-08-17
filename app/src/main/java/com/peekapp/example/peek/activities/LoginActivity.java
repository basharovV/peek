package com.peekapp.example.peek.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.peekapp.example.peek.R;

import java.io.IOException;


public class LoginActivity extends Activity {

    ImageButton fbButton;
    ImageButton googleButton;
    ProgressBar loginProgressBar;
    ImageView loginLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
        setContentView(R.layout.activity_login);

        fbButton = (ImageButton) findViewById(R.id.fbLoginButton);
        loginLogo = (ImageView) findViewById(R.id.loginLogo);
        loginProgressBar = (ProgressBar) findViewById(R.id.login_progress);
        loginProgressBar.setAlpha(0);

        addButtonListener(fbButton);

        // Create global configuration and initialize ImageLoader with this config

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).
                defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheOnDisk(true)
                .build())
                .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            YoYo.with(Techniques.FadeInDown)
                    .duration(1500)
                    .playOn(loginLogo);
        }
    }

    public void addButtonListener(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Animate progress bar
                ObjectAnimator progressAnimation = ObjectAnimator.ofInt(loginProgressBar, "progress", 0, 100);
                progressAnimation.setDuration(2000);
                progressAnimation.setInterpolator(new DecelerateInterpolator());
                loginProgressBar.animate().alpha(100).setDuration(200).start();
                progressAnimation.start();
                Intent pagerIntent = new Intent(LoginActivity.this, PeekViewPager.class);
                startActivity(pagerIntent);
            }
        });
    }
}
