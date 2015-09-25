package com.peekapps.peek.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.peekapps.peek.R;

/**
 * Created by Slav on 19/08/2015.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private LoginButton logoutButton;
    private ListView settingsList;

    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;

    //Modify the list of settings here
    private static final String[] SETTINGS_LIST = new String[]{
                "General",
                "Location",
                "Sharing",
                "Feedback",
                "About Peek"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        settingsToolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settingsToolbar.setTitle("Settings");
        settingsList = (ListView) findViewById(R.id.settingsList);
        logoutButton = (LoginButton) findViewById(R.id.settingsFBButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log out of Facebook
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logOut();

                //Go back to LoginActivity
                Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
    }


    }