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
 * MainActivity handles UI initialization and coordinates media playback and file selection.
 */
public class MainActivity extends AppCompatActivity {
    
    private VideoView videoView;
    private ProgressBar progressBar;
    private TextView textStatus;
    private TextView textFileName;
    private TextInputEditText editTextUrl;
    
    private MaterialButton btnPlay, btnPause, btnStop, btnRestart;
    private MaterialButton btnPickVideo, btnOpenFile, btnOpenUrl;
    
    private MediaControllerHelper mediaControllerHelper;
    private static final int REQUEST_PICK_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
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
        
        mediaControllerHelper = new MediaControllerHelper(videoView, progressBar, textStatus);

        setupListeners();
    }

    private void setupListeners() {
        // Playback control button listeners
        btnPlay.setOnClickListener(v -> mediaControllerHelper.play());
        btnPause.setOnClickListener(v -> mediaControllerHelper.pause());
        btnStop.setOnClickListener(v -> mediaControllerHelper.stop());
        btnRestart.setOnClickListener(v -> mediaControllerHelper.restart());
        
        // Pick video from device storage
        btnPickVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_PICK_VIDEO);
        });
        
        // Open file using system document picker
        btnOpenFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"audio/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, REQUEST_PICK_VIDEO);
        });
        
        // Load media from URL
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                mediaControllerHelper.loadMedia(uri);
                textFileName.setText(uri.getLastPathSegment());
                videoView.setVisibility(View.VISIBLE);
                findViewById(R.id.textVideoPlaceholder).setVisibility(View.GONE);
            }
        }
    }
}
