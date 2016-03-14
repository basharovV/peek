/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;

import com.peekapps.peek.data.audio.AudioUtils;
import com.peekapps.peek.presentation.Presenter;
import com.peekapps.peek.presentation.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Slav on 01/03/2016.
 */
public class CameraPresenter implements Presenter {

    //Fragment has two modes: capture and publish (publish when picture taken)
    static final int CAPTURE_MODE = 0;
    static final int EDIT_MODE = 1;
    static final int SHARE_MODE = 2;
    static final int UPLOAD_MODE = 4;

    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mode = CAPTURE_MODE;   // Current mode

    // ------ CAPTURE MODE -------
    // Flash
    static final String FLASH_ON = Camera.Parameters.FLASH_MODE_ON;
    static final String FLASH_OFF = Camera.Parameters.FLASH_MODE_OFF;
    static final String FLASH_AUTO = Camera.Parameters.FLASH_MODE_AUTO;
    private String[] flashModes = {FLASH_OFF, FLASH_ON, FLASH_AUTO};
    private int currentFlashMode = 0;

    // Auto focus
    private static final String AF_AUTO = Camera.Parameters.FOCUS_MODE_AUTO;


    private CameraView cameraView;

    @Inject
    public CameraPresenter() {

    }

    public void setCameraView(CameraView cameraView) {
        this.cameraView = cameraView;
    }

    private void switchUI(int uiMode) {
        switch(uiMode) {
            case CAPTURE_MODE:
                cameraView.switchToCaptureMode();
                break;
            case EDIT_MODE:
                cameraView.switchToEditMode();
                break;
            case SHARE_MODE:
                cameraView.switchToShareMode();
                break;
            case UPLOAD_MODE:
                cameraView.switchToUploadMode();
        }
        mode = uiMode;
    }

    public void onSwitchCamButtonClick() {
        switchCamera();
    }

    public void onFlashButtonClick() {
        int nextFlashMode = getNextFlashModeIndex();
        setFlashMode(flashModes[nextFlashMode]);
        currentFlashMode = nextFlashMode;
    }

    public void onCaptureButtonClick() {
        switchUI(EDIT_MODE);

    }

    public void onCloseButtonClick() {
        if (mode == SHARE_MODE) switchUI(EDIT_MODE);
        else switchUI(CAPTURE_MODE);
    }

    public void onPublishButtonClick() {
        if (mode == EDIT_MODE) switchUI(SHARE_MODE);
        else if (mode == SHARE_MODE) {
            switchUI(UPLOAD_MODE);
        }


        // ----------------FETCH MOST LIKELY LOCATION FROM GOOGLE PLACES API -----------------
        //Temp - mock location to Times Square
//            PlaceActions.getInstance().setMockLocation(getActivity());
//        try {
//            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
//                    apiClient, null);
//            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                @Override
//                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
//                    com.google.android.gms.location.places.Place mostLikelyPlace = PlaceActions.getInstance(getContext()).
//                            getMostLikelyPlace(likelyPlaces);
//                    if (mostLikelyPlace != null) {
//                        updatePlaceName(mostLikelyPlace.getName().toString());
//                        updatePlace(mostLikelyPlace);
//                    }
//                    likelyPlaces.release();
//                }
//            });
//        }
//        catch (SecurityException e) {
//            Log.e(TAG, "No location permission");
//        }

    }

    private int getNextFlashModeIndex() {
        return currentFlashMode < (flashModes.length - 1)
                ? currentFlashMode + 1 : 0;
    }

    //Media type - to be implemented
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    public void onPhotoTaken(byte[] data) {
        //Save the photo and start publisher mode
//                savePhotoBytes(data);
        switchUI(EDIT_MODE);
    }

    public void switchCamera() {
        if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        cameraView.switchCamera(cameraID);
    }

    private void stopPreview() {
        cameraView.stopPreview();
    }

    private void startPreview() {
        cameraView.startPreview();
    }

    public void disableCamera() {
        cameraView.disableCamera();
    }

    public void enableCamera() {
        cameraView.enableCamera();
    }

    public void setFlashMode(String flashMode) {
        cameraView.setFlashButtonStyle(flashMode);
        cameraView.setFlashMode(flashMode);
    }

//    private void savePhotoBytes(byte[] data) {
//        Bitmap photo = yuvToBitmap(data);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(270);
//        Bitmap rotatedPhoto = Bitmap.createBitmap(photo, 0, 0,
//                photo.getWidth(), photo.getHeight(), matrix, true);
//        ByteBuffer buffer = ByteBuffer.allocate(rotatedPhoto.getByteCount());
//        rotatedPhoto.copyPixelsToBuffer(buffer);
//        photoBytes = buffer.array();
//
//        //Generate bitmap
////        BitmapFactory.Options options = new BitmapFactory.Options();
////        options.inSampleSize = 2;
////        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
////        Matrix matrix = new Matrix();
////        matrix.postRotate(90);
////        Bitmap toPublish = Bitmap.createBitmap(bmp, 0, 0,
////                bmp.getWidth(), bmp.getHeight(), matrix, true);
////        bmp.recycle();
//
//        try {
//            photoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            if (photoFile != null) {
//                if (photoFile.exists())
//                    photoFile.delete();
//                else {
//                    photoFile.createNewFile();
//                }
//
////          Save the photo file
////          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                FileOutputStream outputStream = new FileOutputStream(photoFile);
//                rotatedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
////          photoBytes = outputStream.toByteArray();
//
//            }
//        } catch (IOException e) {
//            Log.d("PhotoReq", "IOException");
//        } catch (NullPointerException e) {
//            Log.e(TAG, "NPE");
//        }
//    }

//    private Bitmap yuvToBitmap(byte[] data) {
//        Camera.Parameters params = camera.getParameters();
//        Camera.Size previewSize = params.getPreviewSize();
//        YuvImage yuvImage = new YuvImage(data, params.getPreviewFormat(),
//                previewSize.width, previewSize.height, null);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, outputStream);
//        byte[] photoBytes = outputStream.toByteArray();
//        return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
//    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
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

    public int getMode() {
        return mode;
    }
}
