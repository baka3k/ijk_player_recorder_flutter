package com.baka3k.stream.player.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLException;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baka3k.stream.player.IjkMediaPlayerUtil;
import com.baka3k.stream.player.player.view.IPlayer;
import com.baka3k.stream.player.recorder.IRecord;
import com.daasuu.gpuv.egl.filter.GlFilter;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public class FilterVideoView extends RelativeLayout implements IPlayer, IRecord {
    private static final String TAG = "FilterVideoView";
    private IMediaPlayer mMediaPlayer = null;
    protected GPUPlayerView mGPUPlayerView;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private Runnable mRunnableHideLoading;
    private boolean isPrepareDataSource = false;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private long mTimeCurrentPosition = 1;
    private long mTimeCurrentWhenExport = -1;
    private boolean isStream = false;
    private long mTimePause;
    private long mDuration;
    private String mUrl;

    private boolean mAutoStart;
    private GlFilter mGlFilter;

    private Runnable reSeekAfterExport = () -> {
        long currentTime = mMediaPlayer.getCurrentPosition();
        if (currentTime == 0) {
            seekTo(mTimeCurrentWhenExport);
        }
        mTimeCurrentWhenExport = -1;
    };

    private final GPUPlayerView.GPUPlayerViewListener mGPUPlayerViewListener = surface -> {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(surface);
            }
        } catch (Exception ex) {
            Log.d("VideoPlay", "setSurface error: " + ex.getMessage());
        }
    };

    private final IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoSarNum = mp.getVideoSarNum();
            mVideoSarDen = mp.getVideoSarDen();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mGPUPlayerView != null) {
//                    mGPUPlayerView.setGlFilter(mGlFilter);
                    int degree = mGPUPlayerView.getDegree();
                    int gpuWidth;
                    int gpuHeight;
                    if (degree == 90 || degree == 270 || degree == -90) {
                        gpuWidth = mVideoHeight;
                        gpuHeight = mVideoWidth;
                    } else {
                        gpuWidth = mVideoWidth;
                        gpuHeight = mVideoHeight;
                    }
                    if (mGPUPlayerView.getVideoWidth() != gpuWidth || mGPUPlayerView.getVideoHeight() != gpuHeight) {
                        mGPUPlayerView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        mGPUPlayerView.setVideoSize(gpuWidth, gpuHeight, mDegree);
                    }
                } else {
                    Log.w(TAG, "#onVideoSizeChanged()mGPUPlayerView null");
                }
            }
        }
    };

    private final IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mGPUPlayerView != null) {
//                    mGPUPlayerView.setGlFilter(mGlFilter);
                    int degree = mGPUPlayerView.getDegree();
                    int gpuWidth;
                    int gpuHeight;
                    if (degree == 90 || degree == 270 || degree == -90) {
                        gpuWidth = mVideoHeight;
                        gpuHeight = mVideoWidth;
                    } else {
                        gpuWidth = mVideoWidth;
                        gpuHeight = mVideoHeight;
                    }
                    if (mGPUPlayerView.getVideoWidth() != gpuWidth || mGPUPlayerView.getVideoHeight() != gpuHeight) {
                        mGPUPlayerView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        mGPUPlayerView.setVideoSize(gpuWidth, gpuHeight, mDegree);
                    }
                }
            }

            if (mAutoStart) {
                start();
            }

            if (mRunnableHideLoading != null) {
                mRunnableHideLoading.run();
            }

            mDuration = mMediaPlayer.getDuration();

            if (!isStream && mTimeCurrentPosition != 1 && mTimeCurrentPosition > 0 && mTimeCurrentPosition <= mDuration) {
                seekTo(mTimeCurrentPosition);
            }

            isPrepareDataSource = true;
        }
    };

    public GlFilter getCurrentFilter() {
        return mGPUPlayerView.getCurrentFilter();
    }

    private final IMediaPlayer.OnCompletionListener mCompletionListener = mp -> {
        Log.d(TAG, "OnCompletionListener: ");
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private final IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
            switch (arg1) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                    Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//                    mVideoRotationDegree = arg2;
                    Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_RESOURCE_NOT_FOUND:
                    Log.d(TAG, "MEDIA_ERROR_RESOURCE_NOT_FOUND:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_IO:
                    Log.d(TAG, "MEDIA_ERROR_IO:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                    Log.d(TAG, "MEDIA_ERROR_MALFORMED:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.d(TAG, "MEDIA_ERROR_UNKNOWN:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.d(TAG, "MEDIA_ERROR_SERVER_DIED:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:");
                    break;
                case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    Log.d(TAG, "MEDIA_ERROR_TIMED_OUT:");
                    break;
            }
            return true;
        }
    };

    private final IMediaPlayer.OnErrorListener mErrorListener = (mp, framework_err, impl_err) -> {
        Log.d(TAG, "Error: " + framework_err + "," + impl_err);
        releasePlayer();
        setDataSource(mUrl);
        initFilterView();
        return true;
    };

    private final IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = (mp, percent) -> {
//            mCurrentBufferPercentage = percent;
        Log.d(TAG, "mBufferingUpdateListener: " + percent);
    };

    private final IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = mp -> {

    };

    private final IMediaPlayer.OnTimedTextListener mOnTimedTextListener = (mp, text) -> {
    };

    public void setMediaPlayer(IMediaPlayer player) {
        if (mMediaPlayer == null) {
            mMediaPlayer = player;
        }
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setCallbackWhenPlayerPrepared(Runnable runnable) {
        if (mRunnableHideLoading == null) {
            mRunnableHideLoading = runnable;
        }
    }

    public FilterVideoView(@NonNull Context context) {
        this(context, null);
    }

    public FilterVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    private FilterVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initPlayer(String url) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = IjkMediaPlayerUtil.createFilePlayer(url);
            }

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            if (url.startsWith("rtsp")) {
                if (isStream) {
                    IjkMediaPlayerUtil.setOptionsIjkPlayer((IjkMediaPlayer) mMediaPlayer);
                } else {
                    IjkMediaPlayerUtil.setOptionsIjkPlayerForPlayback((IjkMediaPlayer) mMediaPlayer);
                }
            }
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initFilterView() {
        removeAllViews();
        if (mGPUPlayerView != null) {
            mGPUPlayerView.onPause();
            //mGPUPlayerView = null;
        }

        if (mGPUPlayerView == null) {
            mGPUPlayerView = new GPUPlayerView(getContext(), mGPUPlayerViewListener);
            RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mGPUPlayerView.setLayoutParams(layoutParams);
        }

        addView(mGPUPlayerView);
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void prepareDataSource(String url) {
        AsyncTaskPrepare mAsyncTaskTest = new AsyncTaskPrepare(url, this);
        mAsyncTaskTest.execute();
    }

    public void prepareDataSourceOnMainThread(String url) {
        mAutoStart = true;
        mUrl = url;
        setDataSource(url);
        initFilterView();
        isStream = true;
    }

    public void muteAudio() {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            IjkMediaPlayer jikPlayer = (IjkMediaPlayer) mMediaPlayer;
            IjkTrackInfo[] trackInfos = jikPlayer.getTrackInfo();
            if (trackInfos != null && trackInfos.length != 0) {
                int i = 0;
                for (IjkTrackInfo trackinfo : trackInfos) {
                    if (trackinfo.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                        jikPlayer.deselectTrack(i);
                    }
                    i++;
                }
            }
        } else {
            Log.w(TAG, "#controlAudio() mMediaPlayer is not IJKPlayer");
        }
    }

    @Override
    public void prepareDataSource(String url, boolean autoStartAfterPrepared) {
        mUrl = url;
        mAutoStart = autoStartAfterPrepared;
        AsyncTaskPrepare mAsyncTaskTest = new AsyncTaskPrepare(url, this);
        mAsyncTaskTest.execute();
    }

    @Override
    public void pause() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
            mTimePause = System.currentTimeMillis();
        } catch (Exception ex) {
            Log.e("MediaPlayer", "Pause fail: " + ex.getMessage());
        }
    }

    public void pause(Runnable runnable) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                new AsyncTaskReset(this, runnable).execute();
            } else {
                mMediaPlayer.pause();
            }
        }
    }

    @Override
    public void play() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer instanceof IjkMediaPlayer) {
                long timeCurrent = System.currentTimeMillis();
                if (timeCurrent - mTimePause + mTimeCurrentPosition > mDuration) {
                    prepareDataSource(mUrl, true);
                } else {
                    seekTo(mTimeCurrentPosition);
                    start();
                }
            } else {
                start();
            }
        }
    }

    public void stop(Runnable runnable) {
        new AsyncTaskReset(this, runnable).execute();
        mTimeCurrentPosition = 0;
    }

    public void stopPlayer() {
        if (mMediaPlayer != null) {
            isPrepareDataSource = false;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            if (mMediaPlayer instanceof AndroidMediaPlayer) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    public void suspend() {
        removePlayer();
        isPrepareDataSource = false;
    }

    public void unSuspend(long timeCurrent, boolean haveFiltered, GlFilter glFilter) {
        mGPUPlayerView.onResume();
        mTimeCurrentWhenExport = timeCurrent;
        releasePlayer();
        setDataSource(mUrl);
        seekTo(timeCurrent);

        getHandler().removeCallbacks(reSeekAfterExport);
        getHandler().postDelayed(reSeekAfterExport, 600L);

        if (!haveFiltered) {
            setFilter(new GlFilter());
        } else {
            setFilter(glFilter);
        }
    }

    public void reTryVideoSource(long time, boolean haveFiltered, GlFilter glFilter) {
        releasePlayer();
        setDataSource(mUrl);
        initFilterView();
        seekTo(time);
        if (!haveFiltered) {
            setFilter(new GlFilter());
        } else {
            setFilter(glFilter);
        }
    }

    @Override
    public void release() {

    }

    public void release(Runnable runnable) {
        new AsyncTaskRelease(this, runnable).execute();
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            mTimeCurrentPosition = position;
            mMediaPlayer.seekTo(position);
        }
    }

    public int getTimeCurrentPosition() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer instanceof AndroidMediaPlayer) {
                long currentFromLib = mMediaPlayer.getCurrentPosition();
                if (currentFromLib > mTimeCurrentPosition) {
                    mTimeCurrentPosition = currentFromLib;
                }

                if (mTimeCurrentPosition > mDuration) mTimeCurrentPosition = currentFromLib;

                return (int) mTimeCurrentPosition;
            }
            mTimeCurrentPosition = mMediaPlayer.getCurrentPosition();
            return (int) mTimeCurrentPosition;
        }
        return 0;
    }

    public void setTimeCurrentPosition(long timeCurrentPosition) {
        mTimeCurrentPosition = timeCurrentPosition;
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer = null;
        }
    }

    public void setFilter(GlFilter filter) {
        mGlFilter = filter;
        if (mGPUPlayerView != null) {
            mGPUPlayerView.setGlFilter(mGlFilter);
        } else {
            Log.w(TAG, "#setFilter()mGPUPlayerView null");
        }
    }

    @Override
    public void startRecord(String filepath) {
        if (mMediaPlayer != null) {
            mMediaPlayer.startRecord(filepath);
        }
    }

    @Override
    public void stopRecord() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stopRecord();
        }
    }

    private void setDataSource(String url) {
        try {
            initPlayer(url);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + url, ex);
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    private static final class AsyncTaskPrepare extends AsyncTask<Void, Void, Void> {
        private final String mURL;
        private final WeakReference<FilterVideoView> mFilterVideoViewWF;

        AsyncTaskPrepare(String url, FilterVideoView filterVideoView) {
            mFilterVideoViewWF = new WeakReference<>(filterVideoView);
            mURL = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FilterVideoView filterVideoView = mFilterVideoViewWF.get();
            if (filterVideoView != null) {
                filterVideoView.setDataSource(mURL);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            FilterVideoView filterVideoView = mFilterVideoViewWF.get();
            if (filterVideoView != null) {
                filterVideoView.initFilterView();
            }
        }
    }

    private static final class AsyncTaskRelease extends AsyncTask<Void, Void, Void> {
        private final WeakReference<FilterVideoView> mFilterVideoViewWF;
        private final Runnable callbackRunnable;

        AsyncTaskRelease(FilterVideoView filterVideoView, Runnable runnable) {
            mFilterVideoViewWF = new WeakReference<>(filterVideoView);
            callbackRunnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FilterVideoView filterVideoView = mFilterVideoViewWF.get();
            if (filterVideoView != null) {
                filterVideoView.releasePlayer();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callbackRunnable.run();
        }
    }

    private static final class AsyncTaskReset extends AsyncTask<Void, Void, Void> {
        private final WeakReference<FilterVideoView> mFilterVideoViewWF;
        private final Runnable callbackRunnable;

        AsyncTaskReset(FilterVideoView filterVideoView, Runnable runnable) {
            mFilterVideoViewWF = new WeakReference<>(filterVideoView);
            callbackRunnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FilterVideoView filterVideoView = mFilterVideoViewWF.get();
            if (filterVideoView != null) {
                filterVideoView.stopPlayer();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callbackRunnable != null) {
                callbackRunnable.run();
            }
        }
    }

    public void setPrepareDataSource(boolean prepare) {
        isPrepareDataSource = prepare;
    }

    public boolean isPrepareDataSource() {
        return isPrepareDataSource;
    }

    private int mDegree;

    public void rotate(int degree) {
        this.mDegree = degree;
        if (mGPUPlayerView != null) {
            mGPUPlayerView.setDegree(degree);
            mGPUPlayerView.setVideoSampleAspectRatio(mVideoSarDen, mVideoSarNum);
            if (degree == 90 || degree == 270 || degree == -90) {
                mGPUPlayerView.setVideoSize(mVideoHeight, mVideoWidth, mDegree);
            } else {
                mGPUPlayerView.setVideoSize(mVideoWidth, mVideoHeight, mDegree);
            }
        } else {
            Log.w(TAG, "#rotate()mGPUPlayerView null");
        }
    }

    public void setFlipVertical(boolean flipVertical) {
        if (mGPUPlayerView != null) {
            mGPUPlayerView.setFlipVertical(flipVertical);
        } else {
            Log.w(TAG, "#setFlipVertical()mGPUPlayerView null");
        }
    }

    public void setFlipHorizontal(boolean flipHorizontal) {
        if (mGPUPlayerView != null) {
            mGPUPlayerView.setFlipHorizontal(flipHorizontal);
        } else {
            Log.w(TAG, "#setFlipHorizontal()mGPUPlayerView null");
        }
    }

    public void removePlayer() {
        if (mGPUPlayerView != null) {
            mGPUPlayerView.onPause();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
        }
    }

    public void capturePhotoFrame(String outputPathPhoto) {
        mGPUPlayerView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            Bitmap snapshotBitmap = createBitmapFromGLSurface(mGPUPlayerView.getMeasuredWidth(), mGPUPlayerView.getMeasuredHeight(), gl);
            try {
                OutputStream fOut = new FileOutputStream(outputPathPhoto);
                if (snapshotBitmap != null) {
                    snapshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                } else {
                    Log.w(TAG, "#capturePhotoFrame() bitmap null");
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "#capturePhotoFrame() err: " + e);
            }
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }
}