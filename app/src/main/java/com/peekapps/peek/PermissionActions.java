package com.peekapps.peek;

import android.*;
import android.Manifest.permission;
import android.Manifest.permission_group;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Slav on 26/10/2015.
 */
public class PermissionActions {

    private static final int REQUEST_CODE = 0;

    public static String PERMISSION_CAMERA = permission.CAMERA;
    public static String PERMISSION_FINE_LOCATION = permission.ACCESS_FINE_LOCATION;
    public static String PERMISSION_READ_STORAGE = permission.READ_EXTERNAL_STORAGE;
    public static String PERMISSION_WRITE_STORAGE = permission.WRITE_EXTERNAL_STORAGE;
    public static String PERMISSION_INTERNET = permission.INTERNET;
    public static String PERMISSION_NETWORK_STATE = permission.ACCESS_NETWORK_STATE;
    public static String PERMISSION_WIFI_STATE = permission.ACCESS_WIFI_STATE;
    public static String PERMISSION_FLASHLIGHT = permission.FLASHLIGHT;

    private static String[] permissions = {PERMISSION_CAMERA,
            PERMISSION_FINE_LOCATION, PERMISSION_FLASHLIGHT,
            PERMISSION_INTERNET, PERMISSION_NETWORK_STATE, PERMISSION_WIFI_STATE,
            PERMISSION_READ_STORAGE, PERMISSION_WRITE_STORAGE};

    public static ArrayList<String> getMissingPermissions(Context context) {
            ArrayList<String> notGranted = new ArrayList<String>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    notGranted.add(permission);
                }
            }
            //Return true if all permissions have been granted
            return notGranted;
    }

    public static void requestMissingPermissions(Context context, ArrayList<String> permissions) {
        // Should we show an explanation?
        //            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
        //                    permission)) {
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context,
                    permissions.toArray(new String[permissions.size()]),
                    REQUEST_CODE);
        }
    }
}
