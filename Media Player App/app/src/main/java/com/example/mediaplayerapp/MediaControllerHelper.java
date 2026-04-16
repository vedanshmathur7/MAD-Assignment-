package com.example.mediaplayerapp; // Package declaration for the app

import android.net.Uri; // Import for media resource location
import android.view.View; // Import for controlling UI visibility
import android.widget.ProgressBar; // Import for the loading bar
import android.widget.TextView; // Import for status text display
import android.widget.VideoView; // Import for video playback view

public class MediaControllerHelper { // Class to handle media playback logic
    
    private final VideoView videoView; // Reference to the VideoView in UI
    private final ProgressBar progressBar; // Reference to the ProgressBar in UI
    private final TextView textStatus; // Reference to the status TextView in UI

    public MediaControllerHelper(VideoView videoView, ProgressBar progressBar, TextView textStatus) { // Constructor
        this.videoView = videoView; // Initialize the videoView reference
        this.progressBar = progressBar; // Initialize the progressBar reference
        this.textStatus = textStatus; // Initialize the textStatus reference
        setupVideoView(); // Configure listeners for the video player
    }

    private void setupVideoView() { // Method to set up media event listeners
        videoView.setOnPreparedListener(mp -> { // Called when media is ready to play
            progressBar.setVisibility(View.GONE); // Hide the loading progress bar
            textStatus.setText("Ready"); // Update status text to "Ready"
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> { // Called if an error occurs during playback
            progressBar.setVisibility(View.GONE); // Hide the loading progress bar
            textStatus.setText("Playback error"); // Show error message in status text
            return true; // Indicate that the error has been handled
        });

        videoView.setOnCompletionListener(mp -> { // Called when the media finishes playing
            textStatus.setText("Playback completed"); // Update status text to "Completed"
        });
    }

    public void loadMedia(Uri uri) { // Method to prepare media for playback from a URI
        textStatus.setText("Loading..."); // Update status to inform user
        progressBar.setVisibility(View.VISIBLE); // Show the loading progress bar
        videoView.setVideoURI(uri); // Set the media source path
        videoView.requestFocus(); // Request focus to handle playback properly
    }

    public void play() { // Method to start or resume playback
        if (!videoView.isPlaying()) { // Check if not already playing
            videoView.start(); // Start the video
            textStatus.setText("Playing"); // Update status text
        }
    }

    public void pause() { // Method to pause playback
        if (videoView.isPlaying()) { // Check if currently playing
            videoView.pause(); // Pause the video
            textStatus.setText("Paused"); // Update status text
        }
    }

    public void stop() { // Method to stop playback entirely
        if (videoView.isPlaying()) { // Check if playing
            videoView.stopPlayback(); // Stop the playback
            textStatus.setText("Stopped"); // Update status text
        } else {
            videoView.suspend(); // Suspend the player if it's not active
            textStatus.setText("Stopped"); // Update status text
        }
    }

    public void restart() { // Method to restart the media from the beginning
        videoView.resume(); // Resume the video view state
        videoView.seekTo(0); // Move playback position to the start (0ms)
        videoView.start(); // Start playing immediately
        textStatus.setText("Playing"); // Update status text
    }
}
