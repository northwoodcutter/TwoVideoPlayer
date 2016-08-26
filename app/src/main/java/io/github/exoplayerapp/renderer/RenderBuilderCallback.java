package io.github.exoplayerapp.renderer;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;

public interface RenderBuilderCallback {

    public void onRender(MediaCodecVideoTrackRenderer videoRenderer,
                         MediaCodecAudioTrackRenderer audioRenderer);

    public void onRenderFailure(Exception e);
}
