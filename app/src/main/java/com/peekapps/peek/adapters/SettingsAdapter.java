package com.peekapps.peek.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Slav on 24/08/2015.
 */
public class SettingsAdapter extends ArrayAdapter<String> {

    private String[] settingsList;

    public SettingsAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }

    private static class ViewHolder {
        private TextView settingsTitle;
        private TextView settingsSubtitle;
    }
}
