package com.peekapp.example.peek.place_api;

import com.peekapp.example.peek.fragments.MapFragment;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Slav on 02/06/2015.
 */
public class PlacesFetchedEvent extends EventObject {

    public PlacesFetchedEvent(Object source) {
        super(source);
    }

}
