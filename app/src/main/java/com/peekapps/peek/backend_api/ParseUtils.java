package com.peekapps.peek.backend_api;

import android.content.Context;
import android.util.Log;
import com.peekapps.peek.facebook_api.FacebookUtils;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.place_api.PlaceActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Slav on 14/01/2016.
 */
public class ParseUtils {

    public static void setUserFBEmail() {
//        String fbEmail = FacebookUtils.getUserEmail();
//        if (fbEmail != null) {
//            ParseUser user = ParseUser.getCurrentUser();
//            user.setEmail(fbEmail);
//            if (user.isAuthenticated()) {
//                user.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Log.d("ParseUtils", "User email saved");
//                        }
//                        else {
//                            Log.d("ParseUtils", "Problem saving email");
//                        }
//                    }
//                });
//            }
//        }
    }

//    public static List<Place> toPeekPlaceArray(Context context, List<PlaceObject> places) {
//        List<Place> peekPlaces = new ArrayList<>();
//        for (PlaceObject pl : places) {
//            Place peekPl = new Place();
//            peekPl.setName(pl.getName());
//            peekPl.setLatitude(pl.getGeoPoint().getLatitude());
//            peekPl.setLongitude(pl.getGeoPoint().getLongitude());
//            peekPl.setVicinity(pl.getVicinity());
//            peekPl.setNumberOfPhotos(pl.getNumberOfPhotos());
//            Random random = new Random();
//
//            //User-specific attrs
//            int randomTime = random.nextInt(31);
//            peekPl.setMinutesAgoUpdated(randomTime);
//            //Calculate and set the distance to the place
//            int distance = PlaceActions.getInstance().distanceToPlace(context, peekPl);
//            peekPl.setDistance(distance);
//            //INCREMENTING RANDOMNESS --> Change!
//            int randomPopularity = random.nextInt(places.size());
//            pl.setNumberOfPhotos(randomPopularity);
//
//            peekPlaces.add(peekPl);
//        }
//        return peekPlaces;
//    }

}
