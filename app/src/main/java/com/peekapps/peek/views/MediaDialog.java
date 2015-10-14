package com.peekapps.peek.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.peekapps.peek.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Slav on 16/06/2015.
 */
public class MediaDialog extends Dialog implements Iterator{
    //Photos currently on the device
    private File[] photos;
    private int currentPos = 0;
    private ImageView photo;

    public MediaDialog(Context context) {
        super(context, android.R.style.Theme_Light);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(((FragmentActivity) context).getLayoutInflater().
                inflate(R.layout.dialog_feed_media
                        , null));
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        photo = (ImageView) findViewById(R.id.dialogImage);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNext()) {
                    next();
                }
                else {
                    hide();
                }
            }
        });
        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                hide();
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getDirectory(String placeID) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + "/PeekMedia/" +
                placeID +
                "/");
        photos = dir.listFiles();
    }

    public void reset() {
        currentPos = 0;
        photos = null;
    }


    @Override
    public boolean hasNext() {
        if (photos == null || photos.length == 0) {
            Toast.makeText(getContext(), "No photos available for this location", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        else if (currentPos < photos.length) {
                return true;
            }
        hide();
        reset();
        return false;
    }

    @Override
    public Object next() {
        showPhoto();
        currentPos++;
        return null;
    }

    private void showPhoto() {
        Picasso.with(getContext())
                .load("file://" + photos[currentPos].toString())
                .fit()
                .centerInside()
                .into(photo);
    }

    public void start() {
        show();
        if (hasNext()) {
            next();
        }
        else {
            photo.setImageResource(R.drawable.no_img_bg);
        }
    }
    @Override
    public void remove() {
    }
}
