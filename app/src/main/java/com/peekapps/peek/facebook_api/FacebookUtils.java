package com.peekapps.peek.facebook_api;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Slav on 14/01/2016.
 */
public class FacebookUtils {

    public static String getUserEmail() {
        GraphRequest emailRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                    }
                });
        Bundle emailParam = new Bundle();
        emailParam.putString("fields", "email");
        emailRequest.setParameters(emailParam);
        GraphResponse emailResponse = emailRequest.executeAndWait();
        String email = null;
        try {
            email = emailResponse.getJSONObject().get("email").toString();
            if (emailResponse.getError() != null && email == null) {
                //Facebook error
            }
        }
        catch (JSONException e) {
            Log.e("FacebookUtils", "JSON Error");
        }
        return email;
    }
}
