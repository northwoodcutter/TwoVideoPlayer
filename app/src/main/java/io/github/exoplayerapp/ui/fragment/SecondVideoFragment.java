package io.github.exoplayerapp.ui.fragment;

import android.app.Fragment;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.util.PlayerControl;

import java.io.File;

import io.github.exoplayerapp.R;
import io.github.exoplayerapp.renderer.RenderBuilder;
import io.github.exoplayerapp.renderer.RenderBuilderCallback;
import io.github.exoplayerapp.renderer.SecondVideoRendererBuilder;

/**
 * Created by 1 on 27.07.2014.
 */
public class SecondVideoFragment extends Fragment implements SurfaceHolder.Callback,
        ExoPlayer.Listener, MediaCodecVideoTrackRenderer.EventListener {
    public static final int RENDERER_COUNT = 2;
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_OTHER = 2;
    private static final String TAG = FirstVideoFragment.class.getSimpleName();
    private MediaController mediaController;
    private ExoPlayer player;
    private boolean autoPlay = true;
    private int playerPosition;
    private Uri contentUri;
    private int contentType;
    private String contentId;
    private RendererBuilderCallback callback;
    private SecondVideoRendererBuilder builder;
    private MediaCodecVideoTrackRenderer videoRenderer;
    private VideoSurfaceView videoSurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_video, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.root1);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                }
                return true;
            }
        });
        //contentUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM" + "/Camera" + "/test1.mp4"));
        contentUri = Uri.parse( "android.resource://" + getActivity().getPackageName() + "/" + R.raw.test2);
        builder = (SecondVideoRendererBuilder) getRendererBuilder();
        videoSurfaceView = (VideoSurfaceView) view.findViewById(R.id.second_video);
        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoSurfaceView);
        videoSurfaceView.getHolder().addCallback(this);
        return view;
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            mediaController.show(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        player = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1000, 5000);
        player.addListener(this);
        player.seekTo(playerPosition);

        mediaController.setMediaPlayer(new PlayerControl(player));
        mediaController.setEnabled(true);
        callback = new RendererBuilderCallback();
        builder.buildRender(callback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
        callback = null;
        videoRenderer = null;
    }

    private RenderBuilder getRendererBuilder(){
        return new SecondVideoRendererBuilder((io.github.exoplayerapp.MyActivity) getActivity(), this, contentUri);
    }

    private void onRenderSuccess(RendererBuilderCallback callback,
                                 MediaCodecVideoTrackRenderer videoRenderer, MediaCodecAudioTrackRenderer audioRenderer) {
        if (this.callback != callback) {
            return;
        }
        this.callback = null;
        this.videoRenderer = videoRenderer;
        player.prepare(videoRenderer, audioRenderer);
        maybeStartPlayback();
    }

    private void maybeStartPlayback() {
        Surface surface = videoSurfaceView.getHolder().getSurface();
        if (videoRenderer == null || surface == null || !surface.isValid()) {
            // We're not ready yet.
            return;
        }
        player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        if (autoPlay) {
            player.setPlayWhenReady(true);
            autoPlay = false;
        }
    }

    private void onRenderError(RendererBuilderCallback callback, Exception e) {
        if (this.callback != callback) {
            return;
        }
        this.callback = null;
        onError(e);
    }

    private void onError(Exception e) {
        Log.e(TAG, "Playback failed", e);
        Toast.makeText(getActivity(), "Playback failed", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // Do nothing.
    }

    @Override
    public void onPlayWhenReadyCommitted() {
        // Do nothing.
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        onError(e);
    }

    // MediaCodecVideoTrackRenderer.Listener

    @Override
    public void onVideoSizeChanged(int width, int height) {
        videoSurfaceView.setVideoWidthHeightRatio(height == 0 ? 1 : (float) width / height);
    }

    @Override
    public void onDrawnToSurface(Surface surface) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
        Log.d(TAG, "Dropped frames: " + count);
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        // This is for informational purposes only. Do nothing.
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        // This is for informational purposes only. Do nothing.
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        maybeStartPlayback();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (videoRenderer != null) {
            player.blockingSendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, null);
        }
    }

    class RendererBuilderCallback implements RenderBuilderCallback {

        @Override
        public void onRender(MediaCodecVideoTrackRenderer videoRenderer, MediaCodecAudioTrackRenderer audioRenderer) {
            onRenderSuccess(this, videoRenderer, audioRenderer);
        }

        @Override
        public void onRenderFailure(Exception e) {
            onRenderError(this, e);
        }
    }
}
