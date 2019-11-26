package com.github.axet.androidlibrary.sound;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

public class Headset {
    public static String TAG = Headset.class.getSimpleName();

    public static int FLAGS = MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
    public static long ACTIONS_MAIN = PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_STOP;
    public static long ACTIONS_SKIP = PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
    public static long ACTIONS = ACTIONS_MAIN | ACTIONS_SKIP;
    public MediaSessionCompat msc;

    public static void handleIntent(Headset headset, Intent intent) {
        if (headset == null)
            return;
        MediaButtonReceiver.handleIntent(headset.msc, intent);
    }

    public Headset(Context context, Class cls) {
        Log.d(TAG, "headset mediabutton on");
        ComponentName name = new ComponentName(context, cls);
        msc = new MediaSessionCompat(context, TAG, name, null);
        msc.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                Headset.this.onPlay();
            }

            @Override
            public void onPause() {
                Headset.this.onPause();
            }

            @Override
            public void onStop() {
                Headset.this.onStop();
            }

            @Override
            public void onSkipToNext() {
                Headset.this.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                Headset.this.onSkipToPrevious();
            }
        });
        msc.setFlags(FLAGS);
        msc.setActive(true);
        msc.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, 0, 1).build()); // bug, when after device reboots we have to set playing state to 'playing' to make mediabutton work
    }

    public void setState(boolean playing) {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
                .setActions(ACTIONS)
                .setState(playing ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED, 0, 1);
        msc.setPlaybackState(builder.build());
    }

    public void onPlay() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onSkipToNext() {
    }

    public void onSkipToPrevious() {
    }

    public void release() {
        Log.d(TAG, "headset mediabutton off");
        msc.release();
        msc = null;
    }
}
