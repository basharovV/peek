/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */
package com.peekapps.peek.presentation.common.di;

import com.peekapps.peek.presentation.common.di.components.MapComponent;

/**
 * Interface representing a contract for clients that contains a component for dependency injection.
 */
public interface HasMapComponent<MapComponent> {
  MapComponent getMapComponent();
}
