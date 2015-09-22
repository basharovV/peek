package com.peekapps.peek.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.peekapps.peek.R;

/**
 * Created by Slav on 19/08/2015.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private ListView settingsList;

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
    }


    }
