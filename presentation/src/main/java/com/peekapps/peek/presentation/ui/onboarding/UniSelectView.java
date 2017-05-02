/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.onboarding;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.ui.BaseView;

import java.util.List;

/**
 * Created by Slav on 07/05/2016.
 */
public interface UniSelectView extends BaseView{

    public void setSuggestedUnis(List<University> suggestedUnis);
    public void toast(String text);
}
