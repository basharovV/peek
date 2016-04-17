package com.peekapps.peek.presentation.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.login.LoginActivity;
import com.peekapps.peek.presentation.ui.main.MainActivity;


/**
 * Created by Slav on 15/09/2015.
 */
public class SplashActivity extends Activity{

//    private SignInManager signInManager;
    private static final String TAG = "SplashActivity";

    /**
     * SignInResultsHandler handles the results from sign-in for a previously signed in user.
     */
//    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
//        /**
//         * Receives the successful sign-in result for an already signed in user and starts the main
//         * activity.
//         * @param provider the identity provider used for sign-in.
//         */
//        @Override
//        public void onSuccess(final IdentityProvider provider) {
//            Log.d(TAG, String.format("User sign-in with previous %s provider succeeded",
//                    provider.getDisplayName()));
//
//            // The sign-in manager is no longer needed once signed in.
//            SignInManager.dispose();
//
//            Toast.makeText(SplashActivity.this, String.format("Sign-in with %s succeeded.",
//                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
//
//            AWSMobileClient.defaultMobileClient()
//                    .getIdentityManager()
//                    .loadUserInfoAndImage(provider, new Runnable() {
//                        @Override
//                        public void run() {
//                            goToMain();
//                        }
//                    });
//        }

//        /**
//         * For the case where the user previously was signed in, and an attempt is made to sign the
//         * user back in again, there is not an option for the user to cancel, so this is overriden
//         * as a stub.
//         * @param provider the identity provider with which the user attempted sign-in.
//         */
//        @Override
//        public void onCancel(final IdentityProvider provider) {
//            Log.wtf(TAG, "Cancel can't happen when handling a previously sign-in user.");
//        }
//
//        /**
//         * Receives the sign-in result that an error occurred signing in with the previously signed
//         * in provider and re-directs the user to the sign-in activity to sign in again.
//         * @param provider the identity provider with which the user attempted sign-in.
//         * @param ex the exception that occurred.
//         */
//        @Override
//        public void onError(final IdentityProvider provider, Exception ex) {
//            Log.e(TAG,
//                    String.format("Cognito credentials refresh with %s provider failed. Error: %s",
//                            provider.getDisplayName(), ex.getMessage()), ex);
//
//            Toast.makeText(SplashActivity.this, String.format("Sign-in with %s failed.",
//                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
//            goToLogin();
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Splash);
        super.onCreate(savedInstanceState);

        // do stuff
        goToLogin();

//        FacebookSdk.sdkInitialize(getApplicationContext());
//
//        new Thread(new Runnable() {
//            public void run() {
//                signInManager = SignInManager.getInstance(SplashActivity.this);
//
//                final SignInProvider provider = signInManager.getPreviouslySignedInProvider();
//
//                // if the user was already previously in to a provider.
//                if (provider != null) {
//                    // asynchronously handle refreshing credentials and call our handler.
//                    signInManager.refreshCredentialsWithProvider(SplashActivity.this,
//                            provider, new SignInResultsHandler());
//                } else {
//                    // Asyncronously go to the sign-in page (after the splash delay has expired).
//                    goToLogin();
//                }
//            }
//        }).start();

//        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
//                updateWithToken(newToken);
//            }
//        };
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void goToMain() {
        Intent pagerIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(pagerIntent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
