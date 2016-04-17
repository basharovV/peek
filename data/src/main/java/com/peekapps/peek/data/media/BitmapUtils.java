/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

/**
 * Created by Slav on 03/03/2016.
 */
public class BitmapUtils {

    public static Bitmap generateCircleMarkerBitmap(Bitmap squareBitmap, boolean withShadow) {
        int size = 92;
        int shadow_offset = 3;
        int diameter = (int) Math.sqrt(Math.pow(size, 2) + Math.pow(size, 2));
        Bitmap output = Bitmap.createBitmap(diameter + shadow_offset,
                diameter + shadow_offset, Bitmap.Config.ARGB_8888);
        output.eraseColor(Color.TRANSPARENT);
        Bitmap markerBitmap = Bitmap.createScaledBitmap(squareBitmap, size, size, false);
        Canvas markerCanvas = new Canvas(output);
        markerCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        markerCanvas.drawARGB(0, 0, 0, 0);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        if (withShadow) {
            paint.setShadowLayer(5.0f, (float) shadow_offset, (float) shadow_offset, Color.GRAY);
        }
        float radius = diameter / 2;
        markerCanvas.drawCircle(diameter / 2, diameter / 2,
                radius, paint);
        int iconPos = (diameter - size) / 2;
        Rect rect = new Rect(iconPos, iconPos, diameter, diameter);
        markerCanvas.drawBitmap(markerBitmap, iconPos, iconPos, null);
        markerBitmap.recycle();
        return output;
    }

}
