/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.audio;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Slav on 16/02/2016.
 */
public class AudioUtils {

    public static void playSound(Context context, int resource) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resource);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
        });
        mediaPlayer.start();
    }
}
