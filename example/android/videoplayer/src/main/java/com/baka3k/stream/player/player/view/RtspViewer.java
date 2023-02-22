package com.baka3k.stream.player.player.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baka3k.stream.player.ijkplayer.widget.IjkVideoView;
import com.baka3k.stream.player.recorder.IRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RtspViewer extends IjkVideoView implements IPlayer, IRecord {
    private static final String TAG = "RtspViewer";

    public RtspViewer(@NonNull Context context) {
        super(context);
    }

    public RtspViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RtspViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RtspViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Uri createURI(String url) {
        return Uri.parse(url);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void play() {
        super.start();
    }

    @Override
    public void prepareDataSource(String url) {
        super.setVideoPath(url);
    }

    @Override
    public void prepareDataSource(String url, boolean autoStartAfterPrepared) throws IllegalArgumentException, IOException {

    }


    @Override
    public void seekTo(long position) {
        super.seekTo((int) position);
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }


    @Override
    public void startRecord(String filepath) {
        if (mMediaPlayer != null) {
            File f = new File(filepath);
            if (f.exists()) {
                Log.w(TAG, "#startRecord(): file exists - remove file");
                f.delete();
            }
            mMediaPlayer.startRecord(filepath);
        } else {
            Log.w(TAG, "#startRecord() err: mediaplayer null");
        }
    }

    @Override
    public void stopRecord() {
        if (mMediaPlayer != null) {
            int value = mMediaPlayer.stopRecord();
            Log.d(TAG, "#stopRecord() status " + value);
        } else {
            Log.w(TAG, "#stopRecord() err: MediaPlayer null");
        }
    }

    @Override
    public void release() {
        super.releasePlayer();
    }

    public void getCurrentFrame(Bitmap bitmap) {
        mMediaPlayer.getCurrentFrame(bitmap);
    }

    public void capturePhotoFrame(String outputPathPhoto) {
        try {
            OutputStream fOut = new FileOutputStream(outputPathPhoto);
            Bitmap bitmap = getCurrentFrame();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else {
                Log.w(TAG, "#capturePhotoFrame() bitmap null");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "#capturePhotoFrame() err: " + e);
        }
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public Bitmap getCurrentFrame() {
        int widthFrame = getVideoWidth(); // must be smaller or equal video frame
        int heightFrame = getVideoHeight();  // must be smaller or equal video frame
        Bitmap bitmap = Bitmap.createBitmap(widthFrame, heightFrame, Bitmap.Config.ARGB_8888);
        getCurrentFrame(bitmap);
        return bitmap;
    }

}
