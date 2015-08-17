package com.peekapp.example.peek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.peekapp.example.peek.activities.MediaPublisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Slav on 03/08/2015.
 */
public class RotateAndSaveTask extends AsyncTask<Object,Void,Bitmap>{

    private Context context;
    private static final String TAG = "Camera Fragment";

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    public RotateAndSaveTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        Bitmap bitmap = (Bitmap) objects[0];
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return newBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        savePhoto(bitmap);
        Intent photoIntent = new Intent(context, MediaPublisher.class);
        context.startActivity(photoIntent);
    }

    private void savePhoto(Bitmap bmp) {
        try {
            File photoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (photoFile.exists()) {
                photoFile.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
    }

    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create a media file name
        try {
            String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(context.getExternalCacheDir().getPath() + File.separator +
                        "temp_photo.jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(context.getExternalCacheDir().getPath() + File.separator +
                        "temp_video.mp4");
            } else {
                return null;
            }
            return mediaFile;
        } catch (NullPointerException e) {
            Log.e(TAG, "Error writing to cache");
        }
        return null;
    }

}
