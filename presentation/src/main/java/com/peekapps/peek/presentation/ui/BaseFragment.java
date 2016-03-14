/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.peekapps.peek.presentation.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.peekapps.peek.presentation.common.di.HasCameraComponent;
import com.peekapps.peek.presentation.common.di.HasMapComponent;
import com.peekapps.peek.presentation.common.di.components.ApplicationComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerFragmentComponent;
import com.peekapps.peek.presentation.common.di.components.FragmentComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.FragmentModule;


/**
 * Base {@link android.app.Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

  FragmentComponent fragmentComponent;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }



  /**
   * Shows a {@link android.widget.Toast} message.
   *
   * @param message An string representing a message to be shown.
   */

  protected void showToastMessage(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
  }

  /**
   * Gets a component for dependency injection by its type.
   */
  @SuppressWarnings("unchecked")
  protected <C> C getComponent(Class<C> componentType) {
    if (componentType.getSimpleName().equals("CameraComponent")) {
      return componentType.cast(((HasCameraComponent) getActivity()).getCameraComponent());
    }
    if (componentType.getSimpleName().equals("MapComponent")) {
      return componentType.cast(((HasMapComponent) getActivity()).getMapComponent());
    }
    return null;
  }

  protected ActivityModule getActivityModule() {
    return ((BaseActivity) getActivity()).getActivityModule();
  }
  protected ApplicationComponent getApplicationComponent() {
    return ((BaseActivity) getActivity()).getApplicationComponent();
  }
}
