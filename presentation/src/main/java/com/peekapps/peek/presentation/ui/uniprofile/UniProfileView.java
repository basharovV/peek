/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.uniprofile;


import com.peekapps.peek.presentation.model.PhotoModel;
import com.peekapps.peek.presentation.ui.BaseView;

import java.util.List;

/**
 * Created by Slav on 23/05/2016.
 */
public interface UniProfileView extends BaseView {

    public void setThumbPhotos(List<PhotoModel> photoList);
}
