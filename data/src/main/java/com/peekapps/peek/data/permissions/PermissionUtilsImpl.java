/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.permissions;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Slav on 26/10/2015.
 */
public class PermissionUtilsImpl implements PermissionUtils {

    private static final int REQUEST_CODE = 0;

    public static String PERMISSION_CAMERA = permission.CAMERA;
    public static String PERMISSION_FINE_LOCATION = permission.ACCESS_FINE_LOCATION;
    public static String PERMISSION_READ_STORAGE = permission.READ_EXTERNAL_STORAGE;
    public static String PERMISSION_WRITE_STORAGE = permission.WRITE_EXTERNAL_STORAGE;
    public static String PERMISSION_INTERNET = permission.INTERNET;
    public static String PERMISSION_NETWORK_STATE = permission.ACCESS_NETWORK_STATE;
    public static String PERMISSION_WIFI_STATE = permission.ACCESS_WIFI_STATE;
    public static String PERMISSION_FLASHLIGHT = permission.FLASHLIGHT;

    private int grantedCount = 0;
    private boolean allPermissionsGranted = false;
    private ArrayList<String> notGranted;

    private AppCompatActivity context;

    private static String[] permissions = {PERMISSION_CAMERA,
            PERMISSION_FINE_LOCATION, PERMISSION_FLASHLIGHT,
            PERMISSION_INTERNET, PERMISSION_NETWORK_STATE, PERMISSION_WIFI_STATE,
            PERMISSION_READ_STORAGE, PERMISSION_WRITE_STORAGE};

    @Inject
    public PermissionUtilsImpl(AppCompatActivity context) {
        this.context = context;
    }


    public boolean hasMissingPermissions() {
            if (notGranted == null) notGranted = new ArrayList<String>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    notGranted.add(permission);
                }
            }
            //Return true if there are missing permissions
            return !notGranted.isEmpty();
    }

    public void requestMissingPermissions() {
        // Should we show an explanation?
        //            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
        //                    permission)) {
        if (!notGranted.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    notGranted.toArray(new String[notGranted.size()]),
                    REQUEST_CODE);
        }
    }

    public boolean checkValidPermissions(String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0)
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                } else grantedCount++;
            }
        if (grantedCount == grantResults.length) {
            allPermissionsGranted = true;
        }
        return allPermissionsGranted;
    }
}
