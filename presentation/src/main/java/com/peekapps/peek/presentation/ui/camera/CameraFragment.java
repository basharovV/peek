/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.camera;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingAnimation;
import com.dd.morphingbutton.MorphingButton;
import com.peekapps.peek.data.audio.AudioUtils;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.di.components.CameraComponent;
import com.peekapps.peek.presentation.common.di.components.DaggerFragmentComponent;
import com.peekapps.peek.presentation.common.di.components.FragmentComponent;
import com.peekapps.peek.presentation.common.di.components.MapComponent;
import com.peekapps.peek.presentation.common.di.modules.FragmentModule;
import com.peekapps.peek.presentation.ui.Animations;
import com.peekapps.peek.presentation.ui.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Peek camera
 * Created by Slav on 27/05/2015.
 */
public class CameraFragment extends BaseFragment implements CameraView {

    //Tag for logging in LogCat
    private static final String TAG = CameraFragment.class.getSimpleName();

    // ------------------------- UI --------------------------

    private boolean enabled = false;
    /*
    UI layers for both modes: capture and publish. Used
    for showing/hiding the appropriate UI.
     */
    @Bind(R.id.cameraUI) FrameLayout cameraUI;
    @Bind(R.id.publishUI) FrameLayout publishUI;

    // ------ CAPTURE MODE -------

    @Bind(R.id.cameraPreview)           FrameLayout previewFrame;
    //BUTTONS
    @Bind(R.id.cameraCaptureButton)     ImageButton captureButton;
    @Bind(R.id.cameraFlashButton)       ImageButton flashButton;
    @Bind(R.id.cameraSwitchButton)      ImageButton switchCamButton;
    @Bind(R.id.cameraSettingsButton)    ImageButton settingsButton;

    // ------ PUBLISHER MODE -------

    @Bind(R.id.publisherFrame)          FrameLayout publisherFrame;
    @Bind(R.id.publishButton)           MorphingButton publishButton;
    @Bind(R.id.publisherCloseButton)    ImageButton closeButton;
    @Bind(R.id.publisherPopup)          LinearLayout popup;
    @Bind(R.id.publisherLocationLayout) RelativeLayout locationLayout;
    @Bind(R.id.publisherPlaceName)      TextView placeName;
    @Bind(R.id.publishLocationProgress) ProgressBar locationProgress;
//    @Bind(R.id.publishButton)   EditText captionView;

    boolean popupIsShown = false;

    @Inject
    CameraPresenter cameraPresenter;

    FragmentComponent fragmentComponent;

    //---------------------- ATTRIBUTES ----------------------
    private Camera camera;
    private CameraTextureView preview;
    private float ratio;

    /* Camera ID for switching between front-facing and back-facing.
       Default: Back-facing
      */
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Main fragment view
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, rootView);
//        getComponent(CameraComponent.class).inject(this);
        initializeComponents();
        //Set up caption - disabled for now
//        captionView = (EditText) rootView.findViewById(R.id.captionView);
        setUpCameraUI();
        setUpPublishUI();
        enable();
//        enableLocation();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraPresenter.setCameraView(this);
    }

    private void initializeComponents() {
//        this.getComponent(CameraComponent.class).inject(this);
        fragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .activityModule(getActivityModule())
                .build();
        fragmentComponent.inject(this);
    }

    @Override
    public void setFlashButtonStyle(String flashMode) {
        switch (flashMode) {
            case CameraPresenter.FLASH_OFF:
                flashButton.setAlpha(1f);
                break;
            case CameraPresenter.FLASH_ON:
                flashButton.setAlpha(1f);
                break;
            case CameraPresenter.FLASH_AUTO:
                flashButton.setAlpha(0.5f);
                break;
        }
    }

    /**
     * Set up OnClickListeners for buttons in the camera fragment (swtich camera, flash, capture)
     */
    private void setUpCameraUI() {
        switchCamButton.setAlpha(0.5f);
        flashButton.setAlpha(0.5f);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPresenter.onCaptureButtonClick();
            }
        });

        switchCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamButton.setAlpha(switchCamButton.getAlpha() == 0.5f ? 1 : 0.5f);
                cameraPresenter.onSwitchCamButtonClick();
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPresenter.onFlashButtonClick();
            }
        });

//        //Set up settings button
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
//                startActivity(settingsIntent);
//            }
//        });
    }

    /**
     * Setup the user interface elements for the publisher layout.
     */
    private void setUpPublishUI() {
        //Set up progress bar
        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_fadepulse);
        locationProgress.setVisibility(View.VISIBLE);
        locationProgress.startAnimation(fadeAnimation);

        publishButton.setTextColor(getResources().getColor(R.color.peek_white));
        morphPublishButton(CameraPresenter.EDIT_MODE);

        //Set up publisher button
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPresenter.onPublishButtonClick();
            }
        });

        //Set up close button listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    cameraPresenter.onCloseButtonClick();
            }
        });

        //Set up listener for exiting the publish popup
        publisherFrame.setOnClickListener(new OnClickOutsidePopupListener());
    }



    public void enable() {
        enableCamera();
//        enableLocation();
    }

    public void enableCamera() {
        //Setup the camera
        preview = new CameraTextureView(this.getActivity());
        
        //Setup the preview view and start it
        previewFrame.addView(preview);
    }

    public void disableCamera() {
        stopPreview();
        releaseCamera();
        enabled = false;
    }



//    private void enableLocation() {
//        //Set up GoogleApiClient
//        apiClient = ((PeekViewPager) getActivity()).getApiClient();
//    }

    /**
     * Handle camera and preview instantiation
     */
    @Override
    public void onResume() {
        super.onResume();
//        if (cameraPresenter.getMode() == CameraPresenter.CAPTURE_MODE) {
//            if (Build.VERSION.SDK_INT >= 23 && !enabled) {
//                if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
//                    enableCamera();
//                }
//            } else enableCamera();
//        }
        enabled = true;
    }

    @Override
    public void onPause() {
        super.onPause();
//        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        disableCamera();                // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (camera != null) {
            if (preview != null) {
                previewFrame.removeView(preview);
                preview = null;
            }
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void startPreview() {
        try {
            if (camera != null) {
                setCameraDisplayOrientation(cameraID, camera);
                camera.startPreview();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);
        }
    }

    public void switchCamera(int cameraID) {
        disableCamera();
        this.cameraID = cameraID;
        enableCamera();
        startPreview();
    }

    @Override
    public void setFlashMode(String flashMode) {
        Camera.Parameters params = camera.getParameters();
        stopPreview();
        params.setFlashMode(flashMode);
        camera.setParameters(params);
        startPreview();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraID);
            // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void takePhoto() {
        AudioUtils.playSound(getActivity(), R.raw.shutter);
        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            //Preview snapshot
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                camera.stopPreview();
                cameraPresenter.onPhotoTaken(data);
            }
        });
    }


    public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

        private List<Size> supportedPreviewSizes;
        private Size previewSize;
        private static final String TAG = "Camera Fragment";


        public CameraTextureView(Context context) {
            super(context);
            setSurfaceTextureListener(this);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera = getCameraInstance();
            if (camera != null) {
                if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    camera.setParameters(params);
                }

                // supported preview sizes
                supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
                for (Size str : supportedPreviewSizes)
                    Log.e(TAG, str.width + "/" + str.height);
                if (supportedPreviewSizes != null) {
                    previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
                }

                if (surface == null) {
                    return;
                }

                // stop preview before making changes
                try {
                    camera.stopPreview();
                } catch (Exception e) {
                    //                 ignore: tried to stop a non-existent preview
                }

                // set preview size and make any resize, rotate or reformatting changes here
                // start preview with new settings
                try {
                    if (previewSize.height >= previewSize.width)
                        ratio = (float) previewSize.height / (float) previewSize.width;
                    else
                        ratio = (float) previewSize.width / (float) previewSize.height;

                    //                //Set preview size
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    //
                    //                //Set the picture size
                    Size pictureSize = getOptimalPictureSize();
                    parameters.setPictureSize(pictureSize.width, pictureSize.height);
                    //
                    ////                //Set overall parameters
                    camera.setParameters(parameters);
                    setCameraDisplayOrientation(cameraID, camera);
                    camera.setPreviewTexture(surface);
                    camera.startPreview();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            setDimension(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //Handled by onPause
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

        /**
         * Sets the picture resolution
         * Choose a size from the supported list to match the camera preview aspect ratio
         */

        private Size getOptimalPictureSize() {
            Camera.Parameters params = camera.getParameters();
            Size maxSize = null;
            for (Size size : params.getSupportedPictureSizes()) {
                float sizeRatio = 0;
                //Horizontal orientation
                if (size.width > size.height) {
                    sizeRatio = (float) size.width / (float) size.height;
                }
                //Vertical orientation
                else {
                    sizeRatio = (float) size.height / (float) size.width;
                }
                if (sizeRatio == ratio) {
                    if (maxSize == null)
                        maxSize = size;
                    else if ((maxSize.width < size.width)&& ((maxSize.height < size.height))) {
                        maxSize = size;
                    }
                }
            }
            return maxSize;
        }

        private void setDimension(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);


            if (supportedPreviewSizes != null ) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
            }
            if (previewSize != null & camera != null) {

                if (previewSize.height >= previewSize.width)
                    ratio = (float) previewSize.height / (float) previewSize.width;
                    //Landscape only
                else
                    ratio = (float) previewSize.width / (float) previewSize.height;
                //
                float camHeight = (int) (width * ratio);
                float newCamHeight;
                float newHeightRatio;

                if (camHeight < height) {
                    newHeightRatio = (float) height / (float) previewSize.height;
                    newCamHeight = (newHeightRatio * camHeight);
                    Log.e(TAG, camHeight + " " + height + " " + previewSize.height + " " + newHeightRatio + " " + newCamHeight);
                    setMeasuredDimension((int) (width * newHeightRatio), (int) newCamHeight);
                    Log.e(TAG, previewSize.width + " | " + previewSize.height + " | ratio - " + ratio + " | H_ratio - " + newHeightRatio + " | A_width - " + (width * newHeightRatio) + " | A_height - " + newCamHeight);
                } else {
                    newCamHeight = camHeight;
                    setMeasuredDimension(width, (int) newCamHeight);
                    Log.e(TAG, previewSize.width + " | " + previewSize.height + " | ratio - " + ratio + " | A_width - " + (width) + " | A_height - " + newCamHeight);
                }
            }
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (changed) {
                final View rootView = getRootView();

                final int width = right - left;
                final int height = bottom - top;

                int previewWidth = width;
                int previewHeight = height;
                if (previewSize != null) {
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                }

                // Center the child SurfaceView within the parent.
                if (width * previewHeight < height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height / previewHeight;
                    rootView.layout((width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height);
                } else {
                    final int scaledChildHeight = previewHeight * width / previewWidth;
                    rootView.layout(0, (height - scaledChildHeight) / 2,
                            width, (height + scaledChildHeight) / 2);
                }
            }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    /**
     * Set the optimal preview size for the camera
     */
    private void setPreviewSize() {
        List<Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        Size previewSize = Collections.min(sizes, new CompareSizesByArea());

        camera.getParameters().setPreviewSize(previewSize.width, previewSize.height);
    }

    /**
     * Set the appropriate rotation value for the camera
     */
    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        // set the right preview orientation
        camera.setDisplayOrientation(result);

//        // make the camera output a rotated image
//        Camera.Parameters cameraParameters = camera.getParameters();
//        cameraParameters.setRotation(0);
//        camera.setParameters(cameraParameters);
    }
    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.width * lhs.height -
                    (long) rhs.width * rhs.height);
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * All code below belongs to the publisher mode, and switching between the publisher and
     * the camera mode.
     * Changing the UI elements, and handling user interactions to upload the captured photo.
     */

    public void switchToCaptureMode() {
//        photoBytes = null;
        Animations.fadeOut(publishUI, 500);
        Animations.fadeIn(cameraUI, 800);
        startPreview();
    }

    public void switchToEditMode() {
        stopPreview();
        closeButton.setImageResource(R.drawable.exit_preview);
        if (cameraPresenter.getMode() == CameraPresenter.CAPTURE_MODE) {
            publishUI.setAlpha(0);
            Animations.fadeOut(cameraUI, 0);
            Animations.fadeIn(publishUI, 0);
        }
        else {
            hidePublishPopup();
        }
        morphPublishButton(CameraPresenter.EDIT_MODE);

    }

    public void switchToShareMode() {
        closeButton.setImageResource(R.drawable.ic_arrow_back);
        showPublishPopup();
        morphPublishButton(CameraPresenter.SHARE_MODE);
    }

    public void switchToUploadMode() {
        morphPublishButton(CameraPresenter.UPLOAD_MODE);
    }

    private void morphPublishButton(int type) {
        switch (type) {
            case CameraPresenter.EDIT_MODE:
                MorphingButton.Params circle = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius(getResources().getDimensionPixelSize(R.dimen.morph_circle))// 56 dp
                        .width(getResources().getDimensionPixelSize(R.dimen.morph_circle))
                        .height(getResources().getDimensionPixelSize(R.dimen.morph_circle)) // 56 dp
                        .color(getResources().getColor(R.color.peek_white_trans))
                        .strokeColor(getResources().getColor(R.color.peek_orange))
                        .strokeWidth(2)// normal state color
                        .colorPressed(getResources().getColor(R.color.peek_light_grey))
                        .icon(R.drawable.ic_publish);
                // pressed state color
                publishButton.morph(circle);
                break;
            case CameraPresenter.SHARE_MODE:
                MorphingButton.Params rect = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius(getResources().getDimensionPixelSize(R.dimen.morph_rect_radius)) // 56 dp
                        .width(getResources().getDimensionPixelSize(R.dimen.morph_rect_width))
                        .height(getResources().getDimensionPixelSize(R.dimen.morph_rect_height))// 56 dp
                        .color(getResources().getColor(R.color.peek_orange)) // normal state color
                        .colorPressed(getResources().getColor(R.color.peek_orange_secondary)) // pressed state
                        .text("Share");
                publishButton.morph(rect);
                break;
            case CameraPresenter.UPLOAD_MODE:
                MorphingButton.Params done = MorphingButton.Params.create()
                        .duration(500)
                        .cornerRadius(200) // 56 dp
                        .width(getResources().getDimensionPixelSize(R.dimen.morph_circle))
                        .height(getResources().getDimensionPixelSize(R.dimen.morph_circle))
                        .color(getResources().getColor(R.color.peek_green))
                        .icon(R.drawable.ic_done)
                        .animationListener(new MorphingAnimation.Listener() {
                            @Override
                            public void onAnimationEnd() {
                                switchToCaptureMode();
                            }
                        });
                publishButton.morph(done);
        }
    }

    private void showPublishPopup() {
        Animations.fadeIn(popup, 0);
        popupIsShown = true;
    }

    private void hidePublishPopup() {
        Animations.fadeOut(popup, 0);
        popupIsShown = false;
    }

    private class OnPublishListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
        }

    }

    private class OnClickOutsidePopupListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (popupIsShown) {
                hidePublishPopup();
            }
        }
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.BLACK);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth();

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight();

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }

    public class CaptionDragListener implements View.OnDragListener {
        private FrameLayout.LayoutParams layoutParams;
        private static final String TAG = "DragListener";
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch(event.getAction())
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    layoutParams = (FrameLayout.LayoutParams)v.getLayoutParams();
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_STARTED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENTERED");
                    int x_cord = (int) event.getX();
                    int y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_EXITED :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_EXITED");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    layoutParams.leftMargin = x_cord;
                    layoutParams.topMargin = y_cord;
                    v.setLayoutParams(layoutParams);
                    break;

                case DragEvent.ACTION_DRAG_LOCATION  :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_LOCATION");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_ENDED   :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENDED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "ACTION_DROP event");

                    // Do nothing
                    break;
                default: break;
            }
            return true;
        }
    }

//    private void savePhoto(Bitmap bmp) {
//        try {
//            File photoFile = new File(Environment.getExternalStorageDirectory() + File.separator +
//                    Environment.DIRECTORY_PICTURES);
//            FileOutputStream outputStream = new FileOutputStream(photoFile);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            Log.d("PhotoReq", "IOException");
//        }
//    }

    //    private Bitmap drawText(Bitmap bmp) {
//        Bitmap.Config bmpConfig = bmp.getConfig();
//        float scale = getResources().getDisplayMetrics().density;
//        if (bmpConfig == null) {
//            bmpConfig = android.graphics.Bitmap.Config.ARGB_8888;
//        }
//        Canvas canvas = new Canvas(bmp);
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(Color.WHITE);
//        paint.setTextSize((int) (scale * 14));
//        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
//        Rect textBox = new Rect();
//
//        paint.getTextBounds(text, 0, text.length(), textBox);
//        int x = (bmp.getWidth() - textBox.width()) / 2;
//        int y = (bmp.getHeight() - textBox.height()) / 2;
//        canvas.drawText(text, x, y, paint);
//        return bmp;
//    }

    private void updatePlaceName(String name) {
        Animations.fadeOut(locationProgress, 0);
        placeName.setText(name);
        Animations.fadeIn(locationLayout, 0);
    }

    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        //Transfer bytes
        byte[] buffer = new byte[1024];
        int length;
        while((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }
}