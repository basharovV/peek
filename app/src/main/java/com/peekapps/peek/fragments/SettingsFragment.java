package com.peekapps.peek.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.peekapps.peek.R;

/**
 * Created by Slav on 21/12/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
