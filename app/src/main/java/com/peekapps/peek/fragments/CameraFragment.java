package com.peekapps.peek.fragments;


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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.hardware.Camera.Size;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.morphingbutton.MorphingButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.peekapps.peek.Animations;
import com.peekapps.peek.AudioUtils;
import com.peekapps.peek.R;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.activities.SettingsActivity;
import com.peekapps.peek.backend_api.PeekBackend;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.place_api.PlaceActions;

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

/**
 * Peek camera
 * Created by Slav on 27/05/2015.
 */
public class CameraFragment extends Fragment implements OnPermissionsListener{


    //-------------------------INSTANCE VARIABLES FOR THE CAMERA UI --------------------------

    private boolean enabled = false;
    /*
    UI layers for both modes: capture and publish. Used
    for showing/hiding the appropriate UI.
     */
    private FrameLayout cameraUI;
    private FrameLayout publishUI;

    //Fragment has two modes: capture and publish (publish when picture taken)
    private static final int CAPTURE_MODE = 0;
    private static final int PUBLISH_MODE = 1;
    private int mode = CAPTURE_MODE;   //Default mode

    //CAMERA OBJECT PROPERTIES
    private Camera camera;
    private FrameLayout previewFrame;
    private CameraTextureView preview;
    private float ratio;

    /* Camera ID for switching between front-facing and back-facing.
       Default: Back-facing
      */
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    //BUTTONS
    private ImageButton captureButton;
    private ImageButton flashButton;
    private ImageButton switchCamButton;
    private ImageButton settingsButton;

    //FLASH MODE
    private static final String FLASH_ON = Camera.Parameters.FLASH_MODE_ON;
    private static final String FLASH_OFF = Camera.Parameters.FLASH_MODE_OFF;
    private static final String FLASH_AUTO = Camera.Parameters.FLASH_MODE_AUTO;
    private ArrayList<String> flashModes;
    private String flashMode = FLASH_OFF;

    //AUTOFOCUS MODE
    private static final String AF_OFF = Camera.Parameters.FOCUS_MODE_AUTO;


    //Media type - to be implemented
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    //Tag for logging in LogCat
    private static final String TAG = "Camera Fragment";

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //Stop the preview
            camera.stopPreview();

            //Save the photo and start publisher mode
            savePhotoBytes(data);
            switchUI(PUBLISH_MODE);

            //Show a success toast message to the user
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "Picture saved successfully to /Peek Media/" + pictureFile.getName(),
//                    Toast.LENGTH_SHORT).show();
        }
    };

    //-------------------------INSTANCE VARIABLES FOR THE PUBLISHER UI --------------------------

    private FrameLayout publisherFrame;

    private byte[] photoBytes;
    private File photoFile;
//    private ParseFile parseFile;
//    private PhotoObject parsePhoto;

    private ImageView photoView;
    private MorphingButton publishButton;
    private TextView confirmButton;
    private ImageButton placePickerButton;
    private ImageButton closeButton;
    private LinearLayout popup;
    private TextView placeName;
    private TextView changeLocationButton;
    private ProgressBar locationProgress;
    private EditText captionView;

    boolean popupIsShown = false;

    //Place stuff
    private GoogleApiClient apiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Place selectedPlace;

    @Override
    public void onPermissionsGranted() {
        enable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Main fragment view
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);

        //Setup UI mode frame layouts
        cameraUI = (FrameLayout) rootView.findViewById(R.id.cameraUI);
        publishUI = (FrameLayout) rootView.findViewById(R.id.publishUI);

        //-------------------------- CAMERA VIEW SETUP ---------------------------------------

        //Find camera UI views
        switchCamButton = (ImageButton) rootView.findViewById(R.id.cameraSwitchButton);
        flashButton = (ImageButton) rootView.findViewById(R.id.cameraFlashButton);
        captureButton = (ImageButton) rootView.findViewById(R.id.pictureButton);
        settingsButton = (ImageButton) rootView.findViewById(R.id.cameraSettingsButton);
        try {
            previewFrame = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        } catch (NullPointerException e) {
            Log.d("onResume", "NPE in finding view");
            e.printStackTrace();
        }

        //-------------------------- PUBLISH VIEW SETUP ---------------------------------------

        //Set up UI
        publisherFrame = (FrameLayout) rootView.findViewById(R.id.publisherFrame);

        //NOT USED
        //photoView = (ImageView) rootView.findViewById(R.id.publisherPhoto);

        publishButton = (MorphingButton) rootView.findViewById(R.id.publishButton);
        confirmButton = (TextView) rootView.findViewById(R.id.publisherConfirmButton);
        closeButton = (ImageButton) rootView.findViewById(R.id.close_publisher);
        popup = (LinearLayout) rootView.findViewById(R.id.publisherPopup);
        placeName = (TextView) rootView.findViewById(R.id.publisherPlaceName);
        changeLocationButton = (TextView) rootView.findViewById(R.id.publisherLocationButton);
        locationProgress = (ProgressBar) rootView.findViewById(R.id.publishLocationProgress);

        //Set up caption - disabled for now
//        captionView = (EditText) rootView.findViewById(R.id.captionView);
        setUpCameraUI();
        setUpPublishUI();
        enableLocation();
        return rootView;
    }

    /**
     * Set up OnClickListeners for buttons in the camera fragment (swtich camera, flash, capture)
     */
    private void setUpCameraUI() {
        //Set up switchcam button
        switchCamButton.setAlpha(0.5f);
        switchCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setAlpha(v.getAlpha() == 0.5f ? 1 : 0.5f);
                switchCamera();
                }
            });


        //Set up flash button
        flashButton.setAlpha(0.5f);
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    Cycle: 1.Off 2.On 3.Auto
                 */
                switch (flashMode) {
                    case FLASH_OFF:
                        v.setAlpha(1f);
                        setFlashMode(FLASH_ON);

                        break;
                    case FLASH_ON:
                        v.setAlpha(1f);
                        setFlashMode(FLASH_AUTO);

                        break;
                    case FLASH_AUTO:
                        v.setAlpha(0.5f);
                        setFlashMode(FLASH_OFF);

                        break;
                }
            }
        });


        //Set up capture button
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioUtils.playSound(getContext(), R.raw.shutter);
                        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                            //Preview snapshot
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                //Stop the preview
                                camera.stopPreview();
                                //Save the photo and start publisher mode
                                savePhotoBytes(data);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switchUI(PUBLISH_MODE);
                                    }

                                });
//                        // get an image from the camera
//                        camera.takePicture(null, null, pictureCallback);
                            }
                        });
                    }
                }
        );

        //Set up settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    /**
     * Setup the user interface elements for the publisher layout.
     */
    private void setUpPublishUI() {

        //Set up image
//        photoFile = new File(getActivity().getExternalCacheDir() + "/temp_photo.jpg");
//        Picasso.with(getActivity()).load(photoFile)
//                .fit()
//                .centerInside()
//                .into(photoView);
//
//        Picasso.with(getActivity()).invalidate(photoFile);

//        captionView.setFocusable(true);
//        captionView.setClickable(true);
//        captionView.setFocusableInTouchMode(true);
//        captionView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(v);
//                v.startDrag(null, shadowBuilder, null, 0);
//                return false;
//
//            }
//        });
//        captionView.setOnDragListener(new CaptionDragListener());
//        captionView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        //Set up close button listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchUI(CAPTURE_MODE);
                        photoBytes = null;
                    }

                });
            }
        });

        //Set up publisher button
        publishButton.setOnClickListener(new OnPublishListener());

        morph("circle");

        //Set up listener for exiting the publish popup
        publisherFrame.setOnClickListener(new OnClickOutsidePopupListener());

        //Set up popup listeners
        changeLocationButton.setOnClickListener(new OnChangeLocationListener());
        confirmButton.setOnClickListener(new OnConfirmPublishListener());

        YoYo.with(Techniques.FadeOutDown)
                .duration(100)
                .playOn(popup);

        //Set up progress bar
        Animation fadeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_fadepulse);
        locationProgress.setVisibility(View.VISIBLE);
        locationProgress.startAnimation(fadeAnimation);
    }

    public void enable() {
        enableCamera();
        enableLocation();
    }

    private void enableCamera() {
        //Setup the camera
        preview = new CameraTextureView(this.getActivity());
        
        //Setup the preview view and start it
        previewFrame.addView(preview);
    }

    private void disableCamera() {
        stop();
        releaseCamera();
        enabled = false;
    }

    private void enableLocation() {
        //Set up GoogleApiClient
        apiClient = ((PeekViewPager) getActivity()).getApiClient();
    }

    /**
     * Handle camera and preview instantiation
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mode == CAPTURE_MODE) {
            if (Build.VERSION.SDK_INT >= 23 && !enabled) {
                if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
                    enableCamera();
                }
            } else enableCamera();
        }
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

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void start() {
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

    private void switchCamera() {
        disableCamera();

        if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        enableCamera();
//        start();
    }

    private void setFlashMode(String flashMode) {
        Camera.Parameters params = camera.getParameters();
        stop();
        params.setFlashMode(flashMode);
        camera.setParameters(params);
        start();
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


    public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

        private List<Camera.Size> supportedPreviewSizes;
        private Camera.Size previewSize;
        private static final String TAG = "Camera Fragment";


        public CameraTextureView(Context context) {
            super(context);
            setSurfaceTextureListener(this);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera = getCameraInstance();
            if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(params);
            }

            // supported preview sizes
            supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            for(Camera.Size str: supportedPreviewSizes)
                Log.e(TAG, str.width + "/" + str.height);
            if (supportedPreviewSizes != null ) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
            }

            if (surface == null) {
                return;
            }

            // stop preview before making changes
            try {
                camera.stopPreview();
            } catch (Exception e){
//                 ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or reformatting changes here
            // start preview with new settings
            try {
                if(previewSize.height >= previewSize.width)
                    ratio = (float) previewSize.height / (float) previewSize.width;
                else
                    ratio = (float) previewSize.width / (float) previewSize.height;

//                //Set preview size
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(previewSize.width, previewSize.height);
//
//                //Set the picture size
                Camera.Size pictureSize = getOptimalPictureSize();
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
//
////                //Set overall parameters
                camera.setParameters(parameters);
                setCameraDisplayOrientation(cameraID, camera);
                camera.setPreviewTexture(surface);
                camera.startPreview();

            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
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

        private Camera.Size getOptimalPictureSize() {
            Camera.Parameters params = camera.getParameters();
            Camera.Size maxSize = null;
            for (Camera.Size size : params.getSupportedPictureSizes()) {
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

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
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
    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
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
    static class CompareSizesByArea implements Comparator<android.hardware.Camera.Size> {
        @Override
        public int compare(android.hardware.Camera.Size lhs, android.hardware.Camera.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.width * lhs.height -
                    (long) rhs.width * rhs.height);
        }
    }


    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create a media file name
        try {
            String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(getActivity().getExternalCacheDir().getPath() + File.separator +
                        "temp_photo.jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(getActivity().getExternalCacheDir().getPath() + File.separator +
                        "temp_video.mp4");
            } else {
                return null;
            }
            return mediaFile;
        } catch (NullPointerException e) {
            Log.e(TAG, "Error writing to cache");
        }
        return null;
    }

    private void savePhotoBytes(byte[] data) {
        Bitmap photo = yuvToBitmap(data);
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedPhoto = Bitmap.createBitmap(photo, 0, 0,
                photo.getWidth(), photo.getHeight(), matrix, true);
        ByteBuffer buffer = ByteBuffer.allocate(rotatedPhoto.getByteCount());
        rotatedPhoto.copyPixelsToBuffer(buffer);
        photoBytes = buffer.array();

        //Generate bitmap
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap toPublish = Bitmap.createBitmap(bmp, 0, 0,
//                bmp.getWidth(), bmp.getHeight(), matrix, true);
//        bmp.recycle();

        try {
            photoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (photoFile != null) {
                if (photoFile.exists())
                    photoFile.delete();
                else {
                    photoFile.createNewFile();
                }

//          Save the photo file
//          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            rotatedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//          photoBytes = outputStream.toByteArray();

            }
        } catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
    }

    private Bitmap yuvToBitmap(byte[] data) {
        Camera.Parameters params = camera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();
        YuvImage yuvImage = new YuvImage(data, params.getPreviewFormat(),
            previewSize.width, previewSize.height, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, outputStream);
        byte[] photoBytes = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * -------------------------------------------------------------------------------------
     * All code below belongs to the publisher mode, and switching between the publisher and
     * the camera mode.
     * Changing the UI elements, and handling user interactions to upload the captured photo.
     */
    private void switchUI(int mode) {
        switch (mode) {
            case CAPTURE_MODE:
                Animations.fadeOut(publishUI);
                Animations.fadeIn(cameraUI);
                start();
                break;
            case PUBLISH_MODE:
                publishUI.setAlpha(0);
                Animations.fadeOut(cameraUI);
                Animations.fadeIn(publishUI);
                stop();
                break;
        }
    }

    private void showPublishPopup() {
        YoYo.with(Techniques.FadeInUp)
                .duration(200)
                .playOn(popup);
        popupIsShown = true;
//        Animations.getInstance().rotate180(publishButton);
    }

    private void hidePublishPopup() {
        YoYo.with(Techniques.FadeOutDown)
                .duration(200)
                .playOn(popup);
        popupIsShown = false;
//        Animations.getInstance().rotate180(publishButton);
    }

    public void morph(String type) {
        if (type.equals("rect")) {
            MorphingButton.Params rect = MorphingButton.Params.create()
                    .duration(500)
                    .cornerRadius(10) // 56 dp
                    .width(350) // 56 dp
                    .height(200) // 56 dp
                    .color(getResources().getColor(R.color.peek_white)) // normal state color
                    .colorPressed(getResources().getColor(R.color.peek_orange_statusbar_dark)) // pressed state
                    .text("SHARE");

            publishButton.morph(rect);
        }
        else if (type.equals("circle")) {
            MorphingButton.Params circle = MorphingButton.Params.create()
                    .duration(500)
                    .cornerRadius(200) // 56 dp
                    .width(200) // 56 dp
                    .height(200) // 56 dp
                    .color(getResources().getColor(R.color.peek_white_trans)) // normal state color
                    .colorPressed(R.color.peek_orange_statusbar_dark); // pressed state color
            publishButton.morph(circle);
        }
    }

    private class OnPublishListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            morph("rect");
            //VIEW POPUP ANIMATION
            if (!popupIsShown) {
                showPublishPopup();
            }
            else {
                hidePublishPopup();
            }

            // ----------------FETCH MOST LIKELY LOCATION FROM GOOGLE PLACES API -----------------
            //Temp - mock location to Times Square
//            PlaceActions.getInstance().setMockLocation(getActivity());
            try {
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
                        apiClient, null);
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        com.google.android.gms.location.places.Place mostLikelyPlace = PlaceActions.getInstance(getContext()).
                                getMostLikelyPlace(likelyPlaces);
                        if (mostLikelyPlace != null) {
                            updatePlaceName(mostLikelyPlace.getName().toString());
                            updatePlace(mostLikelyPlace);
                        }
                        likelyPlaces.release();
                    }
                });
            }
            catch (SecurityException e) {
                Log.e(TAG, "No location permission");
            }

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

    private class OnChangeLocationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            launchPlacePicker();
        }
    }

    private class OnConfirmPublishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            publish();
//            Toast.makeText(getActivity(),
//                    "Photo uploaded successfully",
//                    Toast.LENGTH_LONG).
//                    show();
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


    private void launchPlacePicker() {
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesNotAvailableException e) {
            Log.e("MediaPublisher", "Google Play Services not available");
        }
        catch (GooglePlayServicesRepairableException e) {
            Log.e("MediaPublisher", "Google Play services repairable");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                com.google.android.gms.location.places.Place pickerResult = PlacePicker.getPlace(data, getActivity());
                Toast.makeText(getActivity(), "Place: " + pickerResult.getName(), Toast.LENGTH_LONG).show();
                updatePlaceName(pickerResult.getName().toString());
                updatePlace(pickerResult);
            }
        }
    }

    private void updatePlace(com.google.android.gms.location.places.Place googlePlace) {
        Object[] toPass = new Object[1];
        toPass[0] = googlePlace;
        new UpdatePlaceTask().execute(toPass);
    }

    private class UpdatePlaceTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            updatePlaceImpl((com.google.android.gms.location.places.Place) params[0]);
            return null;
        }

        private void updatePlaceImpl(com.google.android.gms.location.places.Place googlePlace) {
            selectedPlace = PlaceActions.getInstance(getContext()).getPlace(getActivity(), googlePlace.getId(), false);
        }
    }

    private void updatePlaceName(String name) {
        if (locationProgress.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.FlipOutY).playOn(locationProgress);
            locationProgress.setVisibility(View.GONE);
        }
        placeName.setText(name);
        placeName.setVisibility(View.VISIBLE);
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

    /**
     * 17/09/15
     * Upload object image to Google Cloud Storage bucket 'peekphotos'
     */
    private void publish() {
        try {
            File publishDirectory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/PeekMedia/" +
                    selectedPlace.getID() +
                    "/");

            //To byte output stream (for Parse)
//            FileInputStream byteStream = new FileInputStream(photoFile);


            // =========== UPLOAD TO PARSE ===================

            // Check if place already exists
//            ParseQuery<ParseObject> placeQuery = ParseQuery.getQuery("Place");
//            placeQuery.whereEqualTo("placeId", selectedPlace.getID());
//            placeQuery.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//                    if (e == null) {
//                        Log.d(TAG, "Retrieved " + objects.size() + " place objects");
//                        // Selected place not yet in cloud
//                        if (objects.isEmpty()) {
//                            saveSelectedPlace();
//                        }
//                        uploadPhoto();
//                    }
//                }
//            });


            // =========== UPLOAD TO GCS =====================

//            Object toPass[] = new Object[3];
//            toPass[0] = photoFile;
//            toPass[1] = selectedPlace;
//            toPass[2] = getActivity();
//            UploadTask uploadTask = new UploadTask();
//            uploadTask.execute(toPass);

            // =========== SAVE TO DEVICE ======================

            if (!publishDirectory.exists()) {
                publishDirectory.mkdirs();
            }
            String filename = incrementPhotoCounter(publishDirectory) + ".jpg";
            File publishFile = new File(publishDirectory.getAbsolutePath() + File.separator + filename);
            copy(photoFile, publishFile);

            // ========== UPLOAD TO AWS S3 ===================
            uploadPhoto();
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
        catch (IOException e) {
            Log.e(TAG, "IOException");
        }

    }

    public void uploadPhoto() {
        //UPLOAD TO AWS
//        PeekBackend.getCurrentBackendAdapter(getContext())
//                .uploadFile(photoFile);

        // CREATE A PHOTO OBJECT
//        parsePhoto = new PhotoObject();
//        parsePhoto.setPlaceId(selectedPlace.getID());

        // SAVE THE PHOTO FILE TO THE CLOUD
//        try {
//            byte[] photoBytes = Files.toByteArray(photoFile);
//        parseFile = new ParseFile(selectedPlace.getID() + ".jpg", photoBytes);
//        parseFile.saveInBackground(
//            new SaveCallback() {
//               @Override
//                public void done(ParseException e) {
//                    if (e == null) { //Success
//                        Toast.makeText(getActivity(), "File uploaded to Parse", Toast.LENGTH_LONG).show();
//                        getPhotoObject().setPhotoFile(parseFile); //Set photo reference
//                        savePhotoObject();
//                    } else {
//                        e.printStackTrace();
//                        Toast.makeText(getActivity(), "Error uploading photo", Toast.LENGTH_LONG).show();
//                    }
//                }
//            },
//            new ProgressCallback() {
//                @Override
//                public void done(Integer percentDone) {
//                    Log.d(TAG, "Percent done upload: " + percentDone);
//                }
//            });
//        }
//        catch (IOException ie) {
//            Log.e(TAG, "Error reading file");
//        }
    }

//    public PhotoObject getPhotoObject() {
//        return parsePhoto;
//    }

    public void savePhotoObject() {
//        getPhotoObject().saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Toast.makeText(getActivity(),
//                        e == null ? "Photo object saved" : "Error saving photo", Toast.LENGTH_SHORT)
//                .show();
//            }
//        });
    }
    public void saveSelectedPlace() {
//        PlaceObject parsePlace = new PlaceObject();
//        parsePlace.setId(selectedPlace.getID());
//        parsePlace.setName(selectedPlace.getName());
//        parsePlace.setGeoPoint(selectedPlace.getLatitude(), selectedPlace.getLongitude());
//        parsePlace.setType(selectedPlace.getType());
//        parsePlace.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//
//            }
//        });
    }

    private int incrementPhotoCounter(File src) {
        int counter = 0;
        File[] photos = src.listFiles();
        for (File photo : photos) {
            counter++;
        }
        return counter;
    }
}