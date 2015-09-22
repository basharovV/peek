package com.peekapps.peek.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.peekapps.peek.R;

/**
 * Created by Slav on 19/08/2015.
 */
public class ProfileActivity extends AppCompatActivity {

    private Toolbar profileToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileToolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(profileToolbar);
        profileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
