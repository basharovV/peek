package com.peekapp.example.peek.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.peekapp.example.peek.R;

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
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(((FragmentActivity) context).getLayoutInflater().
                inflate(R.layout.dialog_feed_media
                        , null));
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
        photo.setImageResource(R.drawable.bg_nophoto);
    }


    @Override
    public boolean hasNext() {
        if (photos == null) {
            hide();
            reset();
            Toast.makeText(getContext(), "No photos available for this location", Toast.LENGTH_LONG)
            .show();
            return false;
        }
        else if (currentPos < photos.length) {
                return true;
            }
        return false;
    }

    @Override
    public Object next() {
        showPhoto();
        currentPos++;
        return null;
    }

    private void showPhoto() {
        ((BitmapDrawable) photo.getDrawable()).getBitmap().recycle();
        ImageLoader loader = ImageLoader.getInstance();
        loader.clearDiskCache();
        loader.clearMemoryCache();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheOnDisk(true)
                .build();
        loader.displayImage("file://" + photos[currentPos].toString(), photo, options);
    }

    @Override
    public void remove() {
    }
}
