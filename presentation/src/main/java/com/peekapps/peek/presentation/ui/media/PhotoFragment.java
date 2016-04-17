/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.peekapps.peek.presentation.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Slav on 09/12/2015.
 */
public class PhotoFragment extends Fragment {

    private ImageView likeButton;

    //Test purposes
    private int[] imageResources = new int[] {
            R.drawable.rockerfeller1,
            R.drawable.rockerfeller2,
            R.drawable.rockerfeller3,
            R.drawable.rockerfeller4 };

    private PhotoView photoView;
    private int resource = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_photopager_photo, container, false);
        photoView = (PhotoView) rootView.findViewById(R.id.photoPagerPhoto);
        Bundle args = getArguments();
        resource = args.getInt("res");
        Glide.with(getContext())
                .load(resource)
                .centerCrop()
                .into(photoView);

        return rootView;
    }
}
