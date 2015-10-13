package com.peekapps.peek.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
        updateWithToken(AccessToken.getCurrentAccessToken());
        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                updateWithToken(newToken);
            }
        };
    }

    private void updateWithToken(AccessToken accessToken) {
        if (accessToken != null) {
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
