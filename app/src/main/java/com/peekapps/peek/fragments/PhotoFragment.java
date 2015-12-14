package com.peekapps.peek.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.peekapps.peek.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Slav on 09/12/2015.
 */
public class PhotoFragment extends Fragment {

    private ImageView photoView;
    private PhotoViewAttacher photoAttacher;
    private int resourcePhoto = R.drawable.rockerfeller1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_photopager_photo, container, false);
        photoView = (ImageView) rootView.findViewById(R.id.photoPagerPhoto);
        photoAttacher = new PhotoViewAttacher(photoView);
        Bundle args = getArguments();
        resourcePhoto = args.getInt("resource");
        Picasso.with(getContext())
                .load(resourcePhoto)
                .into(photoView);

        return rootView;
    }
}
