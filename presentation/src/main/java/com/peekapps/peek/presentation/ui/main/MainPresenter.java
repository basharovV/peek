package com.peekapps.peek.presentation.ui.main;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.peekapps.peek.data.permissions.PermissionUtils;
import com.peekapps.peek.domain.UserLocation;
import com.peekapps.peek.domain.interactor.DefaultSubscriber;
import com.peekapps.peek.domain.interactor.GetUserLocation;
import com.peekapps.peek.domain.interactor.Interactor;
import com.peekapps.peek.presentation.Presenter;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.UIEventBus;
import com.peekapps.peek.presentation.ui.main.events.MainPagerEvent;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Slav on 21/02/2016.
 */
public class MainPresenter implements Presenter {

    public static int DEFAULT_FRAGMENT = 1;

    // Permission stuff (Android 6.0 +)
    private boolean allPermissionsGranted = false;

    public static final int TOPINFO_LOC_IN_PROGRESS = R.string.top_info_wait;
    public static final int TOPINFO_LOC_DONE = R.string.top_info_done;

    private int currentTopInfo = TOPINFO_LOC_IN_PROGRESS;  //Current type of prompt

    @Inject PermissionUtils permissionUtils;

    private MainView mainView;
    private Interactor getUserLocationInteractor;
    private UIEventBus<MainPagerEvent> uiEventBus;

    @Inject
    public MainPresenter(@Named("userLocation") Interactor getUserLocationInteractor,
                         UIEventBus uiEventBus) {
        this.getUserLocationInteractor = getUserLocationInteractor;
        this.uiEventBus = uiEventBus;
        subscribeToPageStates();
    }

    public void setMainView(@NonNull MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize() {
        // Check for permissions
        checkPermissions();
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (permissionUtils.hasMissingPermissions()) {
                showPermissionsPromt();
            }
            else {
                getUserLocation();
            }
        }
    }

    public void requestPermissions() {
        permissionUtils.
                requestMissingPermissions();
    }

    public void onPermissionsResult(int requestCode,
                                    String permissions[], int[] grantResults) {
        if (permissionUtils.checkValidPermissions(permissions, grantResults)) {
            hidePermissionsPromt();
        }
    }





    public void showPermissionsPromt() {
        mainView.showPermissionsSnackBar();
    }

    public void hidePermissionsPromt() {
        mainView.hidePermissionsSnackBar();
    }

    public void showLocationProgress(int progress) {

    }

    public void hideLocationProgress() {
        mainView.hideLocationProgress();
    }

    public void showStatusBar() {
        mainView.showStatusBar();
    }

    public void hideStatusBar() {
        mainView.hideStatusBar();
    }
    /*
    Handle showing and hiding the status bar depending on the current selected page in the ViewPager.
     */

    private void subscribeToPageStates() {
        uiEventBus.observeEvents(MainPagerEvent.class)
                .subscribe(new OnPageSelectedSubscriber());
    }

    private class OnPageSelectedSubscriber implements Action1<MainPagerEvent> {
        @Override
        public void call(MainPagerEvent mainPagerEvent) {
            if (mainPagerEvent.getPageState() == MainPagerEvent.PAGE_SELECTED) {
                if (mainPagerEvent.getPosition() == 2) {
                    showStatusBar();
                } else {
                    hideStatusBar();
                }
            }
        }
    }

    public void showMessage(String message) {
        mainView.showToast(message);
    }

    public void getUserLocation() {
        getUserLocationInteractor.execute(new OnLocationSubscriber());
    }

    public class OnLocationSubscriber extends DefaultSubscriber<UserLocation> {

        @Override
        public void onCompleted() {
            hideLocationProgress();
        }

        @Override
        public void onError(Throwable e) {
            Log.e("MainPresenter", e.toString());
        }

        @Override
        public void onNext(UserLocation userLocation) {
            showMessage(userLocation.getCountry());
        }
    }
    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
