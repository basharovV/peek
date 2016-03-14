/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.permissions;

/**
 * Created by Slav on 02/03/2016.
 */
public interface PermissionUtils {

    public boolean hasMissingPermissions();

    public void requestMissingPermissions();

    public boolean checkValidPermissions(String permissions[], int[] grantResults);
}
