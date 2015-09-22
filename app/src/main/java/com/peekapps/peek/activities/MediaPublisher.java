package com.peekapps.peek.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.peekapps.peek.R;
import com.peekapps.peek.cloud_api.CloudActions;
import com.peekapps.peek.cloud_api.UploadTask;
import com.peekapps.peek.place_api.PlaceActions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

/**
 * Created by Slav on 13/07/2015.
 */
public class MediaPublisher extends Activity{

    private FrameLayout publisherFrame;

    private File photoFile;

    private ImageView photoView;
    private ImageButton publishButton;
    private TextView confirmButton;
    private ImageButton placePickerButton;
    private ImageButton closeButton;
    private LinearLayout popup;
    private TextView placeName;
    private TextView changeLocationButton;
    private ProgressBar locationProgress;

    private boolean popupIsShown = false;

    //Place stuff
    private GoogleApiClient apiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Place selectedPlace;

    private static final String TAG = "MediaPublisher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        //Set up GoogleApiClient
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        apiClient.connect();

        //Set up UI
        publisherFrame = (FrameLayout) findViewById(R.id.publisherFrame);
        photoView = (ImageView) findViewById(R.id.publisherPhoto);
        publishButton = (ImageButton) findViewById(R.id.publishButton);
        confirmButton = (TextView) findViewById(R.id.publisherConfirmButton);
        closeButton = (ImageButton) findViewById(R.id.close_publisher);
        popup = (LinearLayout) findViewById(R.id.publisherPopup);
        placeName = (TextView) findViewById(R.id.publisherPlaceName);
        changeLocationButton = (TextView) findViewById(R.id.publisherLocationButton);
        locationProgress = (ProgressBar) findViewById(R.id.publishLocationProgress);
        //Set up popup
        popup.setAlpha(0);
        setUpUI();
    }

    private void setUpUI() {
        //Set up image
        photoFile = new File(getExternalCacheDir() + "/temp_photo.jpg");
        Picasso.with(getApplicationContext()).load(photoFile)
                .fit()
                .centerInside()
                .into(photoView);

        Picasso.with(getApplicationContext()).invalidate(photoFile);

        //Set up caption
        EditText captionView = (EditText) findViewById(R.id.captionView);
        captionView.setFocusable(true);
        captionView.setClickable(true);
        captionView.setFocusableInTouchMode(true);
        captionView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(v);
                v.startDrag(null, shadowBuilder, null, 0);
                return false;

            }
        });
        captionView.setOnDragListener(new CaptionDragListener());
        captionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Set up close button listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Set up publisher button
        publishButton.setOnClickListener(new OnPublishListener());

        //Set up listener for exiting the publish popup
        publisherFrame.setOnClickListener(new OnClickOutsidePopupListener());

        //Set up popup listeners
        changeLocationButton.setOnClickListener(new OnChangeLocationListener());
        confirmButton.setOnClickListener(new OnConfirmPublishListener());

        //Set up progress bar
        Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_fadepulse);
        locationProgress.startAnimation(fadeAnimation);
    }

    private class OnPublishListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //VIEW POPUP ANIMATION
            if (!popupIsShown) {
                YoYo.with(Techniques.FadeInUp)
                        .duration(250)
                        .playOn(popup);
                popupIsShown = true;
            }
            //Temp - mock location to Times Square
            PlaceActions.getInstance().setMockLocation(getApplicationContext());
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(
                    apiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    float highestLikelihood = 0;
                    Place topPlace = null;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        float currentLikelihood = placeLikelihood.getLikelihood();
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        if (highestLikelihood == 0) {
                            highestLikelihood = placeLikelihood.getLikelihood();
                            topPlace = placeLikelihood.getPlace();
                        } else {
                            if (currentLikelihood > highestLikelihood) {
                                highestLikelihood = currentLikelihood;
                                topPlace = placeLikelihood.getPlace();
                            }
                        }
                    }
                    if (topPlace != null) {
                        updatePlaceName(topPlace.getName().toString());
                        selectedPlace = topPlace;
                    }
                    likelyPlaces.release();
                }
            });
        }
    }

    private class OnClickOutsidePopupListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (popupIsShown) {
                YoYo.with(Techniques.FadeOutDown)
                        .duration(250)
                        .playOn(popup);
                popupIsShown = false;
            }
        }
    }

    private class OnChangeLocationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            launchPlacePicker();
        }
    }

    private class OnConfirmPublishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            publish();
            Toast.makeText(getApplicationContext(),
                    "Photo uploaded successfully",
                    Toast.LENGTH_LONG).
                    show();
            onBackPressed();
        }
    }
    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.BLACK);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth();

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight();

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas);
        }
    }

    public class CaptionDragListener implements View.OnDragListener {
        private FrameLayout.LayoutParams layoutParams;
        private static final String TAG = "DragListener";
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch(event.getAction())
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    layoutParams = (FrameLayout.LayoutParams)v.getLayoutParams();
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_STARTED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENTERED");
                    int x_cord = (int) event.getX();
                    int y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_EXITED :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_EXITED");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    layoutParams.leftMargin = x_cord;
                    layoutParams.topMargin = y_cord;
                    v.setLayoutParams(layoutParams);
                    break;

                case DragEvent.ACTION_DRAG_LOCATION  :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_LOCATION");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_ENDED   :
                    Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENDED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "ACTION_DROP event");

                    // Do nothing
                    break;
                default: break;
            }
            return true;
        }
    }
    private void savePhoto(Bitmap bmp) {
        try {
            File photoFile = new File(Environment.getExternalStorageDirectory() + File.separator +
                    Environment.DIRECTORY_PICTURES);
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d("PhotoReq", "IOException");
        }
    }

//    private Bitmap drawText(Bitmap bmp) {
//        Bitmap.Config bmpConfig = bmp.getConfig();
//        float scale = getResources().getDisplayMetrics().density;
//        if (bmpConfig == null) {
//            bmpConfig = android.graphics.Bitmap.Config.ARGB_8888;
//        }
//        Canvas canvas = new Canvas(bmp);
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(Color.WHITE);
//        paint.setTextSize((int) (scale * 14));
//        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
//        Rect textBox = new Rect();
//
//        paint.getTextBounds(text, 0, text.length(), textBox);
//        int x = (bmp.getWidth() - textBox.width()) / 2;
//        int y = (bmp.getHeight() - textBox.height()) / 2;
//        canvas.drawText(text, x, y, paint);
//        return bmp;
//    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        photoView.setImageBitmap(null);
        super.onBackPressed();
        onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void launchPlacePicker() {
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getApplicationContext()), PLACE_PICKER_REQUEST);
        }
        catch (GooglePlayServicesNotAvailableException e) {
            Log.e("MediaPublisher", "Google Play Services not available");
        }
        catch (GooglePlayServicesRepairableException e) {
            Log.e("MediaPublisher", "Google Play services repairable");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place pickerResult = PlacePicker.getPlace(data, this);
                Toast.makeText(this, "Place: " + pickerResult.getName(), Toast.LENGTH_LONG).show();
                selectedPlace = pickerResult;
                updatePlaceName(selectedPlace.getName().toString());
            }
        }
    }

    private void updatePlaceName(String name) {
        if (locationProgress.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.FlipOutY).playOn(locationProgress);
            locationProgress.setVisibility(View.GONE);
        }
        placeName.setText(name);
        placeName.setVisibility(View.VISIBLE);
    }
    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        //Transfer bytes
        byte[] buffer = new byte[1024];
        int length;
        while((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

    /**
     * 17/09/15
     * Upload object image to Google Cloud Storage bucket 'peekphotos'
     */
    private void publish() {
        try {
            File publishDirectory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + "/PeekMedia/" +
                    selectedPlace.getId() +
                    "/");
//            //new - upload to Google Cloud Storage
//            Object toPass[] = new Object[3];
//            toPass[0] = photoFile;
//            toPass[1] = selectedPlace;
//            toPass[2] = this;
//            UploadTask uploadTask = new UploadTask();
//            uploadTask.execute(toPass);

            /**
             * Old publisher - save to device
             */
            if (!publishDirectory.exists()) {
                publishDirectory.mkdirs();
            }
            String filename = incrementPhotoCounter(publishDirectory) + ".jpg";
            File publishFile = new File(publishDirectory.getAbsolutePath() + File.separator + filename);
            copy(photoFile, publishFile);
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
        catch (IOException e) {
            Log.e(TAG, "IOException");
        }

    }

    private int incrementPhotoCounter(File src) {
        int counter = 0;
        File[] photos = src.listFiles();
        for (File photo : photos) {
            counter++;
        }
        return counter;
    }
}
