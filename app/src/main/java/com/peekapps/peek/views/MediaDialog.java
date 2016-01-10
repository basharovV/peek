package com.peekapps.peek.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.peekapps.peek.R;
import com.peekapps.peek.adapters.PhotoPagerAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Slav on 16/06/2015.
 */
public class MediaDialog extends DialogFragment{
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

    //    public void getDirectory(String placeID) {
//        File dir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES).getAbsolutePath()
//                + "/PeekMedia/" +
//                placeID +
//                "/");
//        photos = dir.listFiles();
//    }

//    public void reset() {
//        currentPos = 0;
//        photos = null;
//    }


//    @Override
//    public boolean hasNext() {
//        if (photos == null || photos.length == 0) {
//            Toast.makeText(getContext(), "No photos available for this location", Toast.LENGTH_LONG)
//                    .show();
//            return false;
//        }
//        else if (currentPos < photos.length) {
//                return true;
//            }
//        hide();
//        reset();
//        return false;
//    }

//    @Override
//    public Object next() {
//        showPhoto();
//        currentPos++;
//        return null;
//    }

    private void showPhoto() {
        Picasso.with(getContext())
                .load("file://" + photos[currentPos].toString())
                .fit()
                .centerInside()
                .into(photo);
    }

//    public void start() {
//        show();
//        if (hasNext()) {
//            next();
//        }
//        else {
//            photo.setImageResource(R.drawable.no_img_bg);
//        }
//    }
//    @Override
//    public void remove() {
//    }
}
