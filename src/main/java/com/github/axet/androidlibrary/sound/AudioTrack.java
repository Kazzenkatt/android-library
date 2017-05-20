package com.github.axet.androidlibrary.sound;

import android.os.Handler;

public class AudioTrack extends android.media.AudioTrack {
    int markerInFrames = -1;
    int periodInFrames = 1000;
    public long playStart = 0;
    Handler playbackHandler = new Handler();
    Runnable playbackUpdate;
    OnPlaybackPositionUpdateListener playbackListener;

    // old phones bug.
    // http://stackoverflow.com/questions/27602492
    //
    // with MODE_STATIC setNotificationMarkerPosition not called
    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) throws IllegalArgumentException {
        super(streamType, sampleRateInHz, channelConfig, audioFormat, getMinSize(sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes), MODE_STREAM);
    }

    // AudioTrack unable to play shorter then 'min' size of data, fill it with zeros
    public static int getMinSize(int sampleRate, int c, int audioFormat, int b) {
        int min = android.media.AudioTrack.getMinBufferSize(sampleRate, c, audioFormat);
        if (b < min)
            b = min;
        return b;
    }

    void playbackListenerUpdate() {
        if (playbackListener == null)
            return;
        if (playStart <= 0)
            return;

        int mark = 0;
        try {
            mark = getNotificationMarkerPosition();
        } catch (IllegalStateException ignore) { // Unable to retrieve AudioTrack pointer for getMarkerPosition()
        }

        if (mark <= 0 && markerInFrames >= 0) { // some old bugged phones unable to set markers
            playbackHandler.removeCallbacks(playbackUpdate);
            playbackUpdate = new Runnable() {
                @Override
                public void run() {
                    long now = System.currentTimeMillis();
                    if (markerInFrames >= 0) {
                        long playEnd = playStart + markerInFrames * 1000 / getSampleRate();
                        if (now >= playEnd) {
                            playbackListener.onMarkerReached(AudioTrack.this);
                            return;
                        }
                    }
                    playbackListener.onPeriodicNotification(AudioTrack.this);
                    long update = periodInFrames * 1000 / getSampleRate();

                    int len = getNativeFrameCount() * 1000 / getSampleRate(); // getNativeFrameCount() checking stereo fine
                    long end = len * 2 - (now - playStart);
                    if (update > end)
                        update = end;

                    playbackHandler.postDelayed(playbackUpdate, update);
                }
            };
            playbackUpdate.run();
        } else {
            playbackHandler.removeCallbacks(playbackUpdate);
            playbackUpdate = null;
        }
    }

    @Override
    public void release() {
        super.release();
        if (playbackUpdate != null) {
            playbackHandler.removeCallbacks(playbackUpdate);
            playbackUpdate = null;
        }
    }

    @Override
    public void play() throws IllegalStateException {
        super.play();
        playStart = System.currentTimeMillis();
        playbackListenerUpdate();
    }

    @Override
    public int setNotificationMarkerPosition(int markerInFrames) {  // do not check != AudioTrack.SUCCESS crash often
        this.markerInFrames = markerInFrames;
        return super.setNotificationMarkerPosition(markerInFrames);
    }

    @Override
    public int setPositionNotificationPeriod(int periodInFrames) {
        this.periodInFrames = periodInFrames;
        return super.setPositionNotificationPeriod(periodInFrames);
    }

    @Override
    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener) {
        super.setPlaybackPositionUpdateListener(listener);
        this.playbackListener = listener;
        playbackListenerUpdate();
    }

    @Override
    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener, Handler handler) {
        super.setPlaybackPositionUpdateListener(listener, handler);
        this.playbackListener = listener;
        if (handler != null) {
            this.playbackHandler.removeCallbacks(playbackUpdate);
            this.playbackHandler = handler;
        }
        playbackListenerUpdate();
    }

}
