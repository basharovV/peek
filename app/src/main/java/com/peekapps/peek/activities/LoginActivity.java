package com.peekapps.peek.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.peekapps.peek.R;
import com.peekapps.peek.fragments.InfoImageFragment;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class LoginActivity extends FragmentActivity {

    FloatingActionButton fbButton;
    CallbackManager callbackManager;


    ImageButton googleButton;
    ImageView loginLogo;
    ImageView loginPhoto;
    ImageView loginTextLogo;
    TextView loginSlogan;
    AutoScrollViewPager scrollInfoPager;

    @Override
    protected void onResume() {
        super.onResume();
        // Facebook - Logs 'install' and 'app activate' App Events.
        printKeyHash(this);
        AppEventsLogger.activateApp(this);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Facebook - Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);


        //Set up header UI elements
        loginLogo = (ImageView) findViewById(R.id.loginLogo);
        loginTextLogo = (ImageView) findViewById(R.id.loginTextLogo);
        loginSlogan = (TextView) findViewById(R.id.loginSlogan);
        loginPhoto = (KenBurnsView) findViewById(R.id.loginPhoto);
        //Fade in header
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(2000);
        fadeIn.setStartOffset(500);

        Picasso.with(this).load(R.drawable.login_chicago)
                .centerCrop()
                .fit()
                .into(loginPhoto);

        loginLogo.setVisibility(View.VISIBLE);
        loginTextLogo.setVisibility(View.VISIBLE);
        loginSlogan.setVisibility(View.VISIBLE);
        loginLogo.startAnimation(fadeIn);
        loginTextLogo.startAnimation(fadeIn);
        loginSlogan.startAnimation(fadeIn);



        //Info slides
        scrollInfoPager = (AutoScrollViewPager) findViewById(R.id.loginInfoPager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        scrollInfoPager.setAdapter(new InfoImageAdapter(fragmentManager));
        scrollInfoPager.setInterval(4000);
        scrollInfoPager.setScrollDurationFactor(3);
        scrollInfoPager.startAutoScroll();
        scrollInfoPager.setOffscreenPageLimit(5);

        /**
         * Facebook login button SDK setup
         */
        fbButton = (FloatingActionButton) findViewById(R.id.fb_login_button);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent pagerIntent = new Intent(LoginActivity.this, PeekViewPager.class);
                startActivity(pagerIntent);
                finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        // Create global configuration and initialize ImageLoader with this config

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).
                defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheOnDisk(true)
                .build())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public class InfoImageAdapter extends FragmentStatePagerAdapter {

        public InfoImageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return getInfoFragment(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private InfoImageFragment getInfoFragment(int position) {
        InfoImageFragment fragment = new InfoImageFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }
}
