package com.peekapps.peek;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.peekapps.peek.views.AutoFitTextureView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2Fragment extends Fragment implements View.OnClickListener {


    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "Camera2BasicFragment";
    /**
     * CAMERA STATES:
     * 0 - STATE_PREVIEW: preview being shown
     * 1 - STATE_WAITING_LOCK: waiting for focus
     * 2 - STATE_WAITING_PRECAPTURE: waiting for precapture exposure state
     * 3 - STATE_WAITING_NON_PRECAPTURE: waiting for state other than above
     * 4 - STATE_PICTURE_TAKEN: picture taken
     */
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    private String cameraId;
    private AutoFitTextureView textureView;
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private CameraCaptureSession previewSession;
    private CameraDevice cameraDevice;
    private Size previewSize;

    private int state = STATE_PREVIEW;
    /**
     * Semaphore is used to prevent app from exiting before closing (releasing) the camera.
     */
    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private ImageReader imageReader;
    private File file;

    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            if (activity != null) {
                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.pictureButton).setOnClickListener(this);
        textureView = (AutoFitTextureView) view.findViewById(R.id.camera_preview);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        file = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
    }



    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pictureButton: {
                takePicture();
                break;
            }
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * Callback for when the camera device changes state
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice = camera;
            createPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
            Activity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    };

    /**
     * Callback for when a still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener onImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            backgroundHandler.post(new ImageSaver(reader.acquireNextImage(), file));
        }
    };


    private CameraCaptureSession.CaptureCallback captureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (state) {
                case STATE_PREVIEW: {
                    //do nothing
                    break;
                }
                case STATE_WAITING_LOCK: {
                    int autoFocusState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == autoFocusState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == autoFocusState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer autoExposureState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (autoExposureState == null ||
                                autoExposureState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            state = STATE_WAITING_NON_PRECAPTURE;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer autoExposureState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (autoExposureState == null ||
                            autoExposureState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            autoExposureState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        state = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer autoExposureState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (autoExposureState == null || autoExposureState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        state = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            process(result);
        }
    };


    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> validSizes = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size size : choices) {
            if (size.getHeight() == size.getWidth() * h / w &&
                    size.getWidth() >= width && size.getHeight() >= height) {
                validSizes.add(size);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (validSizes.size() > 0) {
            return Collections.min(validSizes, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

//
//    private Handler messageHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Activity activity = getActivity();
//            if (activity != null) {
//                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    public static Camera2Fragment newInstance() {
        Camera2Fragment camera2Fragment = new Camera2Fragment();
        camera2Fragment.setRetainInstance(true);
        return camera2Fragment;
    }

    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics camCharacteristics
                        = cameraManager.getCameraCharacteristics(cameraId);
                if (camCharacteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = camCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new Comparator<Size>() {
                            @Override
                            public int compare(Size lhs, Size rhs) {
                                // We cast here to ensure the multiplications won't overflow
                                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                                        (long) rhs.getWidth() * rhs.getHeight());
                            }
                        });
                imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, 2);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

                previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, largest);
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                } else {
                    textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                }
                this.cameraId = cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            new ErrorDialog().show(getFragmentManager(), "dialog");
        }
    }

    private void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening");
            }
            cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening", e);
        }
    }

    private void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            if (previewSession != null) {
                previewSession.close();
                previewSession = null;
            }
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing", e);
        }
        finally {
            cameraOpenCloseLock.release();
        }
    }

    private void createPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            if (cameraDevice == null) {
                                return;
                            }
                            previewSession = session;
                            try {
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                previewRequest = previewRequestBuilder.build();
                                previewSession.setRepeatingRequest(previewRequest, captureCallback,
                                        backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            showToast("Failed");
                        }
                    }, null);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == textureView || null == previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void takePicture() {
        lockFocus();
    }

    private void lockFocus() {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            state = STATE_WAITING_LOCK;
            previewSession.setRepeatingRequest(previewRequestBuilder.build(), captureCallback,
                    backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void runPrecaptureSequence() {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            state = STATE_WAITING_PRECAPTURE;
            previewSession.capture(previewRequestBuilder.build(), captureCallback,
                    backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (activity == null || cameraDevice == null) {
                return;
            }
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                            showToast("Image saved: " + file);
                            unlockFocus();
                        }
                    };

            previewSession.stopRepeating();
            previewSession.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unlockFocus() {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            previewSession.capture(previewRequestBuilder.build(), captureCallback, backgroundHandler);
            state = STATE_PREVIEW;
            previewSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    /**
         * Compares two {@code Size}s based on their areas.
         */
        static class CompareSizesByArea implements Comparator<Size> {

            @Override
            public int compare(Size lhs, Size rhs) {
                // We cast here to ensure the multiplications won't overflow
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                        (long) rhs.getWidth() * rhs.getHeight());
            }

        }

        public static class ErrorDialog extends DialogFragment {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Activity activity = getActivity();
                return new AlertDialog.Builder(activity)
                        .setMessage("This device doesn't support Camera2 API.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        })
                        .create();
            }
        }

    private void showToast(String text) {
        // We show a Toast by sending request message to mMessageHandler. This makes sure that the
        // Toast is shown on the UI thread.
        Message message = Message.obtain();
        message.obj = text;
        messageHandler.sendMessage(message);
    }

    private static class ImageSaver implements Runnable {

        private final Image image;
        private final File file;

        public ImageSaver(Image image, File file) {
            this.image = image;
            this.file = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                output.write(bytes);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                image.close();
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
