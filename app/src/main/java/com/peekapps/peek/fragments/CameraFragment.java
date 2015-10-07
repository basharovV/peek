package com.peekapps.peek.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.hardware.Camera.Size;
import android.widget.ImageButton;

import com.peekapps.peek.R;
import com.peekapps.peek.activities.MediaPublisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Slav on 27/05/2015.
 */
public class CameraFragment extends Fragment {

    //CAMERA OBJECT PROPERTIES
    private Camera camera;
    private CameraPreview preview;
    private float ratio;
    //Default camera ID is rear-facing camera
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    //BUTTONS
    private ImageButton captureButton;
    private ImageButton flashButton;
    private ImageButton switchCamButton;

    //FLASH MODE
    private static final String FLASH_ON = "FLASH_MODE_ON";
    private static final String FLASH_OFF = "FLASH_MODE_OFF";
    private static final String FLASH_AUTO = "FLASH_MODE_AUTO";
    private boolean flashOn = false;
    private String[] flashModes = new String[] {FLASH_ON, FLASH_OFF, FLASH_AUTO};

    private SurfaceHolder surfaceHolder;
    private CameraOrientationListener orientationListener;

    private boolean activeFragment;
    private boolean isPreviewRunning;

    private static final String TAG = "Camera Fragment";

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            orientationListener.rememberOrientation();
            //Generate bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap newBitmap = Bitmap.createBitmap(bmp, 0, 0,
                    bmp.getWidth(), bmp.getHeight(), matrix, true);
            bmp.recycle();
            savePhoto(newBitmap);
            startPublisher();
            //Show a success toast message to the user
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "Picture saved successfully to /Peek Media/" + pictureFile.getName(),
//                    Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Create an instance of Camera


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        orientationListener = new CameraOrientationListener(getActivity());
        activeFragment = true;

        switchCamButton = (ImageButton) rootView.findViewById(R.id.cameraSwitchButton);
        flashButton = (ImageButton) rootView.findViewById(R.id.cameraFlashButton);
        captureButton = (ImageButton) rootView.findViewById(R.id.pictureButton);

        //Setup the camera
        camera = getCameraInstance();
        preview = new CameraPreview(this.getActivity(), camera);
        //Setup the preview view and start it
        try {
            FrameLayout previewLayout = (FrameLayout) rootView.findViewById(R.id.camera_preview);
            previewLayout.addView(preview);
        } catch (NullPointerException e) {
            Log.d("onResume", "NPE in finding view");
            e.printStackTrace();
        }
        start();

        //Initialise and setup all camera buttons/UI
        setUpButtons();

        return rootView;
    }

    /**
     * Handle camera and preview instantiation
     */
    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            camera = getCameraInstance();
            start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();// release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (camera != null) {
            preview.getHolder().removeCallback(preview);
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    public void stop() {
        activeFragment = false;
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void start() {
        try {
            activeFragment = true;
            if (camera != null) {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                isPreviewRunning = true;
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);
        }
    }
    
    /**
     * Set up OnClickListeners for buttons in the camera fragment (swtich camera, flash, capture)
     */
    private void setUpButtons() {
        //Set up switchcam button
        switchCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preview.isEnabled()) {
                    stop();
                    camera.release();

                    if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    else {
                        cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    camera = Camera.open(cameraID);
                    setCameraDisplayOrientation(cameraID, camera);
                    start();
                }
            }
        });

        //Set up flash button
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Camera.Parameters params = camera.getParameters();
                stop();
                if (!flashOn) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flashOn = true;
                }
                else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flashOn = false;
                }
                camera.setParameters(params);
                start();
            }
        });

        //Set up capture button
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        camera.takePicture(null, null, pictureCallback);
                    }
                }
        );
    }

    private class CameraOrientationListener extends OrientationEventListener {
        private int normalisedOrientation;
        private int rememberedOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int i) {
            if (i != ORIENTATION_UNKNOWN) {
                normalisedOrientation = normalize(i);
            }
        }

        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            rememberedOrientation = normalisedOrientation;
        }

        public int getRememberedOrientation() {
            return rememberedOrientation;
        }
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (getView() != null) {
//            isCurrentView = true;
//            if (camera != null) {
//                if (!preview.isActivated()) {
//                    camera.startPreview();
//              }
//            }
//        } else {
//            isCurrentView = false;
//        }
//    }


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


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        private Camera camera;
        private List<Camera.Size> supportedPreviewSizes;
        private Camera.Size previewSize;
        private static final String TAG = "Camera Fragment";

        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.camera = camera;

            // supported preview sizes
            supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            for(Camera.Size str: supportedPreviewSizes)
                Log.e(TAG, str.width + "/" + str.height);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            this.getHolder().removeCallback(this);
            camera.stopPreview();
            camera.release();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (holder.getSurface() == null) {
                return;
            }
            // stop preview before making changes
            try {
                camera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }
            // set preview size and make any resize, rotate or reformatting changes here
            // start preview with new settings
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(previewSize.width, previewSize.height);

                //Set the picture size
                Camera.Size pictureSize = getOptimalPictureSize();
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
                //Set overall parameters
                camera.setParameters(parameters);

                camera.setPreviewDisplay(holder);
                camera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

        /**
         * Sets the picture resolution
         * Choose a size from the supported list to match the camera preview aspect ratio
         */

        private Camera.Size getOptimalPictureSize() {
            Camera.Parameters params = camera.getParameters();
            Camera.Size maxSize = null;
            for (Camera.Size size : params.getSupportedPictureSizes()) {
                float sizeRatio;
                //Horizontal orientation
                if (size.width > size.height) {
                    sizeRatio = size.width / size.height;
                }
                //Vertical orientation
                else {
                    sizeRatio = size.height / size.width;
                }
                if (sizeRatio == ratio) {
                    maxSize = size;
                }
            }
            return maxSize;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

            setMeasuredDimension(width, height);

            if (supportedPreviewSizes != null) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
            }

//            if(previewSize.height >= previewSize.width)
//                ratio = (float) previewSize.height / (float) previewSize.width;
//            else
//                ratio = (float) previewSize.width / (float) previewSize.height;
//
//            float camHeight = (int) (width * ratio);
//            float newCamHeight;
//            float newHeightRatio;
//
//            if (camHeight < height) {
//                newHeightRatio = (float) height / (float) previewSize.height;
//                newCamHeight = (newHeightRatio * camHeight);
//                Log.e(TAG, camHeight + " " + height + " " + previewSize.height + " " + newHeightRatio + " " + newCamHeight);
//                setMeasuredDimension((int) (width * newHeightRatio), (int) newCamHeight);
//                Log.e(TAG, previewSize.width + " | " + previewSize.height + " | ratio - " + ratio + " | H_ratio - " + newHeightRatio + " | A_width - " + (width * newHeightRatio) + " | A_height - " + newCamHeight);
//            } else {
//                newCamHeight = camHeight;
//                setMeasuredDimension(width, (int) newCamHeight);
//                Log.e(TAG, previewSize.width + " | " + previewSize.height + " | ratio - " + ratio + " | A_width - " + (width) + " | A_height - " + newCamHeight);
//            }
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
                if (width * previewHeight > height * previewWidth) {
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
        double targetRatio = (double) h / w;

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

    private void startPreview() {
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);
        }
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
        // make the camera output a rotated image
        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setRotation(0);
        camera.setParameters(cameraParameters);
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

    private void savePhoto(Bitmap bmp) {
        try {
            File photoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (photoFile.exists()) {
                photoFile.delete();
            }
            else{
                photoFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void startPublisher() {
        Intent photoIntent = new Intent(getActivity(), MediaPublisher.class);
        getActivity().startActivity(photoIntent);
    }
}
