package com.peekapps.peek;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.lang.reflect.Method;

/**
 * Created by Slav on 20/09/2015.
 */
public class MockLocationProvider {
    String providerName;
    Context ctx;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        lm.addTestProvider(providerName, false, false, false, false, false, true, true, 0, 5);
        lm.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(double lat, double lon) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());

        try {
            Method method = Location.class.getMethod("makeComplete");
            if (method != null) {
                method.invoke(mockLocation);
            }
        }
        catch (Exception ex){

        }


        lm.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }
}
