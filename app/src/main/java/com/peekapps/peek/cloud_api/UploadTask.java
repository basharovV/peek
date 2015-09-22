package com.peekapps.peek.cloud_api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.places.Place;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Slav on 17/09/2015.
 */
public class UploadTask extends AsyncTask<Object, File, Void> {
    @Override
    protected Void doInBackground(Object... params) {
        try {
            File photo = (File) params[0];
            Place pl  = (Place) params[1];
            Context context = (Context) params[2];
            CloudActions.uploadStream(context, pl.getId(), "image/jpeg", new FileInputStream(photo),
                    "peekphotos");
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("Upload task" , "File not found");
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.e("Upload task", "Security exception with Google App Engine");
        }
        return null;
    }
}
