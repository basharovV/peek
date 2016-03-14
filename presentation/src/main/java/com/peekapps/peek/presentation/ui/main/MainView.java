package com.peekapps.peek.presentation.ui.main;

import com.peekapps.peek.presentation.ui.BaseView;

/**
 * Created by Slav on 21/02/2016.
 */
public interface MainView extends BaseView {

    public void showLocationProgress(int progress);
    public void hideLocationProgress();
    public void showPermissionsSnackBar();
    public void hidePermissionsSnackBar();
    public void hideStatusBar();
    public void showStatusBar();
    public void showToast(String text);
}
