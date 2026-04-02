package com.example.mediaplayerapp;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MediaControllerHelper {
    
    private final VideoView videoView;
    private final ProgressBar progressBar;
    private final TextView textStatus;

    public MediaControllerHelper(VideoView videoView, ProgressBar progressBar, TextView textStatus) {
        this.videoView = videoView;
        this.progressBar = progressBar;
        this.textStatus = textStatus;
        setupVideoView();
    }

    private void setupVideoView() {
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            textStatus.setText("Ready");
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> {
            progressBar.setVisibility(View.GONE);
            textStatus.setText("Playback error");
            return true;
        });

        videoView.setOnCompletionListener(mp -> {
            textStatus.setText("Playback completed");
        });
    }

    public void loadMedia(Uri uri) {
        textStatus.setText("Loading...");
        progressBar.setVisibility(View.VISIBLE);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
    }

    public void play() {
        if (!videoView.isPlaying()) {
            videoView.start();
            textStatus.setText("Playing");
        }
    }

    public void pause() {
        if (videoView.isPlaying()) {
            videoView.pause();
            textStatus.setText("Paused");
        }
    }

    public void stop() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
            textStatus.setText("Stopped");
        } else {
            videoView.suspend();
            textStatus.setText("Stopped");
        }
    }

    public void restart() {
        videoView.resume();
        videoView.seekTo(0);
        videoView.start();
        textStatus.setText("Playing");
    }
}
