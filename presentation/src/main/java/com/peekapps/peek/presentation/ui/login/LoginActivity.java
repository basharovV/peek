/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.ActivityComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerActivityComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.ui.BaseActivity;
import com.peekapps.peek.presentation.ui.main.MainActivity;
import com.peekapps.peek.presentation.ui.onboarding.UniSelectActivity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends BaseActivity {

    private final static String TAG = "LoginActivity";
//    private SignInManager signInManager;

//    private CallbackManager callbackManager;

    @Bind(R.id.loginLogo)               ImageView loginLogo;
    @Bind(R.id.loginTextLogo)           ImageView loginTextLogo;

    @Bind(R.id.loginFacebookButton)     RelativeLayout fbButton;

    @Inject
    InfoImageAdapter loginInfoImageAdapter;
    @Inject
    LoginPresenter loginPresenter;

    ActivityComponent activityComponent;
//
//
//    public static String printKeyHash(Activity context) {
//        PackageInfo packageInfo;
//        String key = null;
//        try {
//            //getting application package name, as defined in manifest
//            String packageName = context.getApplicationContext().getPackageName();
//
//            //Retriving package info
//            packageInfo = context.getPackageManager().getPackageInfo(packageName,
//                    PackageManager.GET_SIGNATURES);
//
//            Log.e("Package Name=", context.getApplicationContext().getPackageName());
//
//            for (Signature signature : packageInfo.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                key = new String(Base64.encode(md.digest(), 0));
//
//                // String key = new String(Base64.encodeBytes(md.digest()));
//                Log.e("Key Hash=", key);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("Name not found", e1.toString());
//        }
//        catch (NoSuchAlgorithmException e) {
//            Log.e("No such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("Exception", e.toString());
//        }
//
//        return key;
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initializeComponent();
        getComponent().inject(this);
        initializeLogin();
//        callbackManager = CallbackManager.Factory.create();
//
//        setupUI();
//        signInManager = SignInManager.getInstance(this);
//
//        signInManager.setResultsHandler(this, new SignInResultsHandler());
//
//        // Initialize sign-in buttons.
//        signInManager.initializeSignInButton(FacebookSignInProvider.class,
//                this.findViewById(R.id.fb_login_button));
    }

    private void initializeComponent() {
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    public ActivityComponent getComponent() {
        return activityComponent;
    }

    private void initializeLogin() {
        //Fade in header
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(2000);
        fadeIn.setStartOffset(500);

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavigator().navigateToUniSelection(LoginActivity.this);
            }
        });

        loginLogo.setVisibility(View.VISIBLE);
        loginTextLogo.setVisibility(View.VISIBLE);
        loginLogo.startAnimation(fadeIn);
        loginTextLogo.startAnimation(fadeIn);

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    public void goToLogin() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    public void goToUniSelection() {
        Intent uniSelectionIntent = new Intent(LoginActivity.this, UniSelectActivity.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(uniSelectionIntent);
        finish();
    }

    public void goToMain() {
        Intent pagerIntent = new Intent(LoginActivity.this, MainActivity.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pagerIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause/resume Mobile Analytics collection
//        AWSMobileClient.defaultMobileClient().handleOnPause();
        // Facebook - Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // pause/resume Mobile Analytics collection
//        AWSMobileClient.defaultMobileClient().handleOnResume();

        // Facebook - Logs 'install' and 'app activate' App Events.
//        printKeyHash(this);
//        AppEventsLogger.activateApp(this);
    }
}
