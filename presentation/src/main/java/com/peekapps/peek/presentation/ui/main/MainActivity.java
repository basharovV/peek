package com.peekapps.peek.presentation.ui.main;

import android.graphics.Camera;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.HasCameraComponent;
import com.peekapps.peek.presentation.common.di.HasMapComponent;
import com.peekapps.peek.presentation.common.di.components.ActivityComponent;
import com.peekapps.peek.presentation.common.di.components.CameraComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerActivityComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerCameraComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerMapComponent;
import com.peekapps.peek.presentation.common.di.components.MapComponent;
import com.peekapps.peek.presentation.common.di.modules.ActivityModule;
import com.peekapps.peek.presentation.common.di.modules.CameraModule;
import com.peekapps.peek.presentation.common.di.modules.MapModule;
import com.peekapps.peek.presentation.ui.Animations;
import com.peekapps.peek.presentation.ui.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Slav on 21/02/2016.
 */
public class MainActivity extends BaseActivity implements MainView {

    @Bind(R.id.mainContainer) FrameLayout mainLayout;
    @Bind(R.id.mainPager) ViewPager mainPager;
    @Bind(R.id.mainTopInfoBar) View infoBar;
    @Bind(R.id.mainTopInfoProgressBG) View infoBarProgress;
    @Bind(R.id.mainTopInfoDoneBG) View infoBarDone;
    @Bind(R.id.mainTopInfoBarText) TextView infoText;

    @Inject MainPagerAdapter mainPagerAdapter;
    @Inject MainScrollListener mainScrollListener;
    @Inject MainPresenter mainPresenter;

    ActivityComponent mainComponent;
//    CameraComponent cameraComponent;
//    MapComponent mapComponent;
    //Map
    //Feed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeComponents();
        mainPresenter.setMainView(this);
        initializeActivity();
        initialize();
    }

    private void initializeComponents() {
        mainComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
//        cameraComponent = DaggerCameraComponent.builder()
//                .applicationComponent(getApplicationComponent())
//                .activityModule(getActivityModule())
//                .cameraModule(new CameraModule())
//                .build();
//        mapComponent = DaggerMapComponent.builder()
//                .applicationComponent(getApplicationComponent())
//                .activityModule(getActivityModule())
//                .mapModule(new MapModule())
//                .build();
        mainComponent.inject(this);

    }

    private void initializeActivity() {
        mainPager.setAdapter(mainPagerAdapter);
        mainPager.addOnPageChangeListener(mainScrollListener);
        mainPager.setCurrentItem(MainPresenter.DEFAULT_FRAGMENT);
        mainPager.setOffscreenPageLimit(2);
    }

    private void initialize() {
        mainPresenter.initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mainPresenter.onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void showLocationProgress(int progress) {
    }

    @Override
    public void hideLocationProgress() {
        Animations.fadeOut(infoBar, 0);
    }

    @Override
    public void showPermissionsSnackBar() {
        Snackbar.make(mainLayout,
                R.string.pager_perm_snackbar, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.pager_perm_action,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mainPresenter.requestPermissions();
                            }
                        }
                )
                .show();
    }

    @Override
    public void hidePermissionsSnackBar() {

    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
