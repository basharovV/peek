/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peekapps.peek.presentation.common.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;


import com.peekapps.peek.presentation.ui.main.MainActivity;
import com.peekapps.peek.presentation.ui.onboarding.UniSelectActivity;
import com.peekapps.peek.presentation.ui.uniprofile.UniProfileActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

  @Inject
  public Navigator() {
    //empty
  }

  /**
   * Goes to the user list screen.
   *
   * @param context A Context needed to open the destiny activity.
   */
  public void navigateToMain(Context context) {
    if (context != null) {
      Intent intentToLaunch = new Intent(context, MainActivity.class);
      context.startActivity(intentToLaunch);
    }
  }

  public void navigateToUniSelection(Context context) {
    if (context != null) {
      Intent uniSelectionIntent = new Intent(context, UniSelectActivity.class).setFlags(
              Intent.FLAG_ACTIVITY_CLEAR_TOP);
      context.startActivity(uniSelectionIntent);
    }
  }

  /**
   * Goes to the user details screen.
   *
   * @param context A Context needed to open the destiny activity.
   */
  public void navigateToUserDetails(Context context, int userId) {
    if (context != null) {
//      Intent intentToLaunch = UserDetailsActivity.getCallingIntent(context, userId);
//      context.startActivity(intentToLaunch);
    }
  }

  public void navigateToUniProfile(Context context, int uniId) {
    Intent uniProfileIntent = new Intent(context, UniProfileActivity.class).
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    uniProfileIntent.putExtra("uni_id", uniId);
    context.startActivity(uniProfileIntent);
  }

  public void navigateToLogin(Context context) {

  }
}
