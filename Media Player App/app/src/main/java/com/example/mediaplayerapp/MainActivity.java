package com.example.mediaplayerapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.content.Intent;
import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * MainActivity serves as the primary user interface for the Media Player application.
 * It handles UI initialization, media playback controls, and file selection.
 */
public class MainActivity extends AppCompatActivity {
    
    // UI Components for video display and status information
    private VideoView videoView;
    private ProgressBar progressBar;
    private TextView textStatus;
    private TextView textFileName;
    private TextInputEditText editTextUrl;
    
    // Playback control buttons
    private MaterialButton btnPlay, btnPause, btnStop, btnRestart;
    
    // Media source selection buttons
    private MaterialButton btnPickVideo, btnOpenFile, btnOpenUrl;
    
    // Helper class to manage media playback logic
    private MediaControllerHelper mediaControllerHelper;
    
    // Request code for picking media files
    private static final int REQUEST_PICK_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components by finding their IDs in the layout
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        textStatus = findViewById(R.id.textStatus);
        textFileName = findViewById(R.id.textFileName);
        editTextUrl = findViewById(R.id.editTextUrl);
        
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnRestart = findViewById(R.id.btnRestart);
        
        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnOpenFile = findViewById(R.id.btnOpenFile);
        btnOpenUrl = findViewById(R.id.btnOpenUrl);
        
        // Initialize the media controller helper with the necessary UI components
        mediaControllerHelper = new MediaControllerHelper(videoView, progressBar, textStatus);

        // Set up click listeners for all buttons
        setupListeners();
    }

    /**
     * Configures the click event handlers for playback and selection buttons.
     */
    private void setupListeners() {
        // Playback control listeners delegated to the MediaControllerHelper
        btnPlay.setOnClickListener(v -> mediaControllerHelper.play());
        btnPause.setOnClickListener(v -> mediaControllerHelper.pause());
        btnStop.setOnClickListener(v -> mediaControllerHelper.stop());
        btnRestart.setOnClickListener(v -> mediaControllerHelper.restart());
        
        // Listener to pick a video file from the device storage
        btnPickVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_PICK_VIDEO);
        });
        
        // Listener to open a document (supporting both audio and video)
        btnOpenFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"audio/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, REQUEST_PICK_VIDEO);
        });
        
        // Listener to load and play media from a provided URL
        btnOpenUrl.setOnClickListener(v -> {
            String url = editTextUrl.getText().toString().trim();
            if(!url.isEmpty()) {
                mediaControllerHelper.loadMedia(Uri.parse(url));
                textFileName.setText(url);
                videoView.setVisibility(View.VISIBLE);
                findViewById(R.id.textVideoPlaceholder).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Handles the result from the file picker or document opener.
     * 
     * @param requestCode The integer request code originally supplied to startActivityForResult().
     * @param resultCode  The integer result code returned by the child activity.
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                // Load the selected media into the player
                mediaControllerHelper.loadMedia(uri);
                // Update the UI to show the selected file name and reveal the video view
                textFileName.setText(uri.getLastPathSegment());
                videoView.setVisibility(View.VISIBLE);
                findViewById(R.id.textVideoPlaceholder).setVisibility(View.GONE);
            }
        }
    }
}
