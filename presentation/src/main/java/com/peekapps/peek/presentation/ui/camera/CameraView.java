/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.camera;

import com.peekapps.peek.presentation.ui.BaseView;

/**
 * Created by Slav on 02/03/2016.
 */
public interface CameraView extends BaseView{

    void switchToCaptureMode();
    void switchToEditMode();
    void switchToShareMode();
    void switchToUploadMode();

    void switchCamera(int camera);
    void setFlashMode(String flashMode);
    void enableCamera();
    void disableCamera();
    void startPreview();
    void stopPreview();

    void setFlashButtonStyle(String flashMode);
}
