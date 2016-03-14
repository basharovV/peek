/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.ui.media.PhotoPager;
import com.peekapps.peek.presentation.ui.media.PhotoPagerAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Slav on 16/06/2015.
 */
public class MediaDialog extends DialogFragment {
    //Photos currently on the device
    private File[] photos;
    private int currentPos = 0;

    private Toolbar mediaToolbar;
    private PhotoPager photoPager;
    private PhotoPagerAdapter photoPagerAdapter;
    private ImageView photo;


    public static MediaDialog newInstance() {
        Bundle args = new Bundle();
        MediaDialog fragment = new MediaDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.MediaDialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.dialog_feed_media, container, false);

        photoPager = (PhotoPager) rootView.findViewById(R.id.dialogPhotoPager);
        photoPagerAdapter = new PhotoPagerAdapter(getChildFragmentManager());
        photoPager.setCurrentItem(0);
        photoPager.setAdapter(photoPagerAdapter);
        photoPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().hide();
            }
        });

        mediaToolbar = (Toolbar) rootView.findViewById(R.id.mediaDialogToolbar);
        mediaToolbar.setTitle("Selected place");
        mediaToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mediaToolbar.setSubtitle("Uploaded 15:38");
        return rootView;
    }


    private void showPhoto() {
        Picasso.with(getContext())
                .load("file://" + photos[currentPos].toString())
                .fit()
                .centerInside()
                .into(photo);
    }
}
