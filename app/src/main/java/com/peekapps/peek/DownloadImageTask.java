package com.peekapps.peek;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;

/**
 * Created by Slav on 21/06/2015.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    ImageView imageView;
    MarkerOptions marker;

    public DownloadImageTask(ImageView imageView, MarkerOptions marker) {
        this.imageView = imageView;
        this.marker = marker;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap placeIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            placeIcon = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            Log.e("Error downloading image", e.getMessage());
            e.printStackTrace();
        }
        return placeIcon;
    }

    @Override
    protected void onPostExecute(Bitmap resultIcon) {
        if (imageView != null) {
            imageView.setImageBitmap(resultIcon);
        }
        else if (marker != null) {
            marker.icon(BitmapDescriptorFactory.fromBitmap(resultIcon));
        }
        }
}
