/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.ui.BaseView;

import java.util.List;

/**
 * Created by Slav on 11/03/2016.
 */
public interface FeedView extends BaseView {

    public void updateFeed(List<University> universityList);
}
