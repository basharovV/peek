package com.peekapps.peek.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.amazonaws.mobile.user.signin.FacebookSignInProvider;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.peekapps.peek.R;
import com.peekapps.peek.fragments.InfoImageFragment;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class LoginActivity extends FragmentActivity {

    private final static String TAG = "LoginActivity";
    private SignInManager signInManager;

    private FloatingActionButton fbButton;
    private CallbackManager callbackManager;

    private ImageButton googleButton;
    private ImageView loginLogo;
    private ImageView loginPhoto;
    private ImageView loginTextLogo;
    private TextView loginSlogan;
    private AutoScrollViewPager scrollInfoPager;


    /**
     * SignInResultsHandler handles the final result from sign in. Making it static is a best
     * practice since it may outlive the SplashActivity's life span.
     */
    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
        /**
         * Receives the successful sign-in result and starts the main activity.
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider) {
            Log.d(TAG, String.format("User sign-in with %s succeeded",
                    provider.getDisplayName()));

            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

            Toast.makeText(LoginActivity.this, String.format("Sign-in with %s succeeded.",
                    provider.getDisplayName()), Toast.LENGTH_LONG).show();

            // Load user name and image.
            AWSMobileClient.defaultMobileClient()
                    .getIdentityManager().loadUserInfoAndImage(provider, new Runnable() {
                @Override
                public void run() {
                    goToMain();
                }
            });
        }

        /**
         * Recieves the sign-in result indicating the user canceled and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            Log.d(TAG, String.format("User sign-in with %s canceled.",
                    provider.getDisplayName()));

            Toast.makeText(LoginActivity.this, String.format("Sign-in with %s canceled.",
                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
        }

        /**
         * Receives the sign-in result that an error occurred signing in and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, final Exception ex) {
            Log.e(TAG, String.format("User Sign-in failed for %s : %s",
                    provider.getDisplayName(), ex.getMessage()), ex);

            final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            errorDialogBuilder.setTitle("Sign-In Error");
            errorDialogBuilder.setMessage(
                    String.format("Sign-in with %s failed.\n%s", provider.getDisplayName(), ex.getMessage()));
            errorDialogBuilder.setNeutralButton("Ok", null);
            errorDialogBuilder.show();
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        setupUI();
        signInManager = SignInManager.getInstance(this);

        signInManager.setResultsHandler(this, new SignInResultsHandler());

        // Initialize sign-in buttons.
        signInManager.initializeSignInButton(FacebookSignInProvider.class,
                this.findViewById(R.id.fb_login_button));
    }

    private void setupUI() {
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
        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    private void goToLogin() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    private void goToMain() {
        Intent pagerIntent = new Intent(LoginActivity.this, PeekViewPager.class).setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pagerIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnPause();
        // Facebook - Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnResume();

        // Facebook - Logs 'install' and 'app activate' App Events.
        printKeyHash(this);
        AppEventsLogger.activateApp(this);
    }
}
