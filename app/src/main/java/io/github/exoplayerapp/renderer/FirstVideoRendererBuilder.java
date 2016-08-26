package io.github.exoplayerapp.renderer;

import android.media.MediaCodec;
import android.net.Uri;

import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;

import io.github.exoplayerapp.MyActivity;
import io.github.exoplayerapp.ui.fragment.FirstVideoFragment;

public class FirstVideoRendererBuilder implements RenderBuilder {

    private final FirstVideoFragment fragment;
    private final Uri uri;
    private MyActivity activity;

    public FirstVideoRendererBuilder(MyActivity activity, FirstVideoFragment playerFragment, Uri uri) {
        this.fragment = playerFragment;
        this.uri = uri;
        this.activity = activity;
    }

    @Override
    public void buildRender(RenderBuilderCallback callback) {
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(fragment.getActivity(), uri, null, 2);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 0, activity.getFirstHandler(),
                fragment, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        callback.onRender(videoRenderer, audioRenderer);
    }
}
