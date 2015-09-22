package com.peekapps.peek.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.Arrays;

/**
 * Created by Slav on 15/09/2015.
 */
public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (accessToken != null || profile != null) {
            Intent pagerIntent = new Intent(MainActivity.this, PeekViewPager.class);
            startActivity(pagerIntent);
        }
        else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        finish();
    }
}
