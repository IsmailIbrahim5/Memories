package com.idea.memories.Views.Fragments;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.memories.Classes.MediaFile;
import com.idea.memories.R;
import com.idea.memories.Views.Activities.MainActivity;

@SuppressLint("ValidFragment")
public class MediaFileFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private ImageView image, extension;
    private TextureView video;
    private MediaFile mediaFile;
    private MediaPlayer videoPlayer, audioPlayer;
    private View playButton;
    public View root;
    private int width , height;
    private boolean playing;
    private OpenedMediaFragment openedMediaFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_media_file, container, false);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = root.getWidth();
                height = root.getHeight();
            }
        });

        image = root.findViewById(R.id.image_src);
        video = root.findViewById(R.id.video_src);
        extension = root.findViewById(R.id.extension_icon);

        video.setSurfaceTextureListener(this);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openedMediaFragment.is_controls_on) {
                    openedMediaFragment.hide_controls();
                    if (mediaFile.getMimeType().contains("video") | mediaFile.getMimeType().contains("audio")) {
                        openedMediaFragment.getFragment().start();
                    }
                } else {
                    openedMediaFragment.show_controls();
                    if (mediaFile.getMimeType().contains("video") | mediaFile.getMimeType().contains("audio"))
                        openedMediaFragment.getFragment().pause();
                }
            }
        };

        try {
            switch (mediaFile.getMimeType()) {
                case "image": ((MainActivity) getActivity()).loadingBitmap(mediaFile, image, height, width);
                    break;

                case "video":
                    video.setVisibility(View.VISIBLE);
                    videoPlayer = new MediaPlayer();
                    try {
                        videoPlayer.setDataSource(getContext(), mediaFile.getPath());
                        videoPlayer.prepare();
                        videoPlayer.seekTo(1);
                    } catch (Exception ignored) { }
                    extension.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            openedMediaFragment.show_controls();
                            pause();
                        }
                    });

                    break;
                case "audio":
                    audioPlayer = new MediaPlayer();
                    try {
                        audioPlayer.setDataSource(getContext(), mediaFile.getPath());
                        audioPlayer.prepare();
                    } catch (Exception e) {
                    }
                    extension.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone));
                    extension.setMinimumHeight(500);
                    extension.setMinimumWidth(500);
                    audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            openedMediaFragment.show_controls();
                            pause();
                        }
                    });
                    break;
            }
        } catch (Exception ignored) { }

        root.setOnClickListener(onClickListener);

        return root;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, final int height) {
        Surface s = new Surface(surface);
        try {
            videoPlayer.setSurface(s);
            videoPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                    video.getLayoutParams().height = height;
                    video.requestLayout();
                }
            });
        } catch (Exception ignored) { }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void pause() {
        if (playing) {
            try {
                AnimatedVectorDrawableCompat pauseToPlay = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_pause_to_play);
                playButton.findViewById(R.id.play_icon).setBackground(pauseToPlay);
                pauseToPlay.start();
                ((TextView) playButton.findViewById(R.id.play_text)).setText("Play");
                if (mediaFile.getMimeType().contains("video")) {
                    videoPlayer.pause();
                    extension.setVisibility(View.VISIBLE);
                } else if (mediaFile.getMimeType().contains("audio")) {
                    audioPlayer.pause();
                }
            } catch (Exception ignored) { }
            playing = false;
        }
    }


    public void start() {
        try {
            AnimatedVectorDrawableCompat playToPause = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_play_to_pause);
            playButton.findViewById(R.id.play_icon).setBackground(playToPause);
            playToPause.start();
            ((TextView) playButton.findViewById(R.id.play_text)).setText("Pause");
            if (mediaFile.getMimeType().contains("video")) {
                videoPlayer.start();
                image.setVisibility(View.GONE);
                extension.setVisibility(View.GONE);
            } else if (mediaFile.getMimeType().contains("audio"))
                audioPlayer.start();
        } catch (Exception ignored) {}
        playing = true;
    }


    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public void setPlayButton(View playButton) {
        this.playButton = playButton;
    }

    public void setOpenedMediaFragment(OpenedMediaFragment openedMediaFragment) {
        this.openedMediaFragment = openedMediaFragment;
    }
}
