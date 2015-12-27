package com.peekapps.peek.map;

import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Slav on 14/12/2015.
 */
public class MapboxTileProvider extends UrlTileProvider {

    private final static String mapBoxUrlFormat =
            "https://api.mapbox.com/v4/%s/%d/%d/%d@2x.png?access_token=%s";

    private final static String MAP_ID = "mapbox.streets";
    private String accessToken;

    public MapboxTileProvider() {
        super(512, 512);
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            final String url = String.format(
                    mapBoxUrlFormat, MAP_ID, z, x, y, accessToken);
            return new URL(url);
        }
        catch (MalformedURLException e) {
            Log.d("MapboxTileProvider", "Malformed URL");
            return null;
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
