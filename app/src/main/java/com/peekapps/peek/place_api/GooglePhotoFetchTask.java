package com.peekapps.peek.place_api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Slav on 01/11/2015.
 */
public class GooglePhotoFetchTask extends AsyncTask<Object, Void, Void> {

    private String API_KEY = "AIzaSyCmBci73i_WF97IB1IKYnIIkXR1_NeECBw";

    @Override
    protected Void doInBackground(Object... params) {
        try {
            Context context = (Context) params[0];
            Place onePlace = (Place) params[1];
            ArrayList<Place> placesList = (ArrayList<Place>) params[2];
            if (placesList != null && onePlace == null) {
                for (Place pl : placesList) {
                    if (pl.hasPhoto())  downloadPhoto(pl, context);
                }
            }
            else if (placesList == null && onePlace != null) {
                if (onePlace.hasPhoto())  downloadPhoto(onePlace, context);
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void downloadPhoto(Place pl, Context context) {

        try {
            StringBuilder urlBuilder = new StringBuilder
                    ("https://maps.googleapis.com/maps/api/place/photo?");
            urlBuilder.append("maxwidth=3000");
            urlBuilder.append("&maxheight=3000");
            urlBuilder.append("&photoreference=" + pl.getPhoto().getPhotoRef());
            urlBuilder.append("&key=" + API_KEY);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //Download bitmap
            Bitmap photo = BitmapFactory.decodeStream(inputStream);
            savePhoto(photo, pl.getID(), context);
            reader.close();
            inputStream.close();
            urlConnection.disconnect();
        }
        catch (MalformedURLException e) {
            Log.d("PhotoReq", "Malformed URL");
        }
        catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        }
    }

    private void savePhoto(Bitmap bmp, String id, Context context) {
        try {
            File photoFile = new File(context.getExternalCacheDir() + "/" + id + "photo.jpg");
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        }
    }
}
