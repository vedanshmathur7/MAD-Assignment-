package com.example.mediaplayerapp; // Defines the package name for the application

import android.net.Uri; // Import for handling Uniform Resource Identifiers (URIs)
import android.os.Bundle; // Import for passing data between Android components
import android.view.View; // Import for basic user interface components
import android.widget.ProgressBar; // Import for showing progress of an operation
import android.widget.TextView; // Import for displaying text to the user
import android.widget.VideoView; // Import for playing video files
import android.content.Intent; // Import for starting new activities or services
import android.app.Activity; // Import for the base class of an Android activity

import androidx.appcompat.app.AppCompatActivity; // Import for backward-compatible activity support

import com.google.android.material.button.MaterialButton; // Import for Material Design buttons
import com.google.android.material.textfield.TextInputEditText; // Import for Material Design text input fields

/**
 * MainActivity serves as the primary user interface for the Media Player application.
 * It handles UI initialization, media playback controls, and file selection.
 */
public class MainActivity extends AppCompatActivity { // Main class inheriting from AppCompatActivity
    
    // UI Components for video display and status information
    private VideoView videoView; // Declares a VideoView for playing video
    private ProgressBar progressBar; // Declares a ProgressBar for showing loading state
    private TextView textStatus; // Declares a TextView for status messages
    private TextView textFileName; // Declares a TextView for the name of the file
    private TextInputEditText editTextUrl; // Declares an input field for video URLs
    
    // Playback control buttons
    private MaterialButton btnPlay, btnPause, btnStop, btnRestart; // Declares buttons for playback control
    
    // Media source selection buttons
    private MaterialButton btnPickVideo, btnOpenFile, btnOpenUrl; // Declares buttons for selecting media sources
    
    // Helper class to manage media playback logic
    private MediaControllerHelper mediaControllerHelper; // Declares the helper for media logic
    
    // Request code for picking media files
    private static final int REQUEST_PICK_VIDEO = 1; // Unique ID for identifying the file picker result

    @Override // Indicates that this method overrides a method in the superclass
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity is first created
        super.onCreate(savedInstanceState); // Calls the superclass implementation of onCreate
        setContentView(R.layout.activity_main); // Sets the user interface layout for this activity

        // Initialize UI components by finding their IDs in the layout
        videoView = findViewById(R.id.videoView); // Links the videoView variable to the layout component
        progressBar = findViewById(R.id.progressBar); // Links the progressBar variable to the layout component
        textStatus = findViewById(R.id.textStatus); // Links the textStatus variable to the layout component
        textFileName = findViewById(R.id.textFileName); // Links the textFileName variable to the layout component
        editTextUrl = findViewById(R.id.editTextUrl); // Links the editTextUrl variable to the layout component
        
        btnPlay = findViewById(R.id.btnPlay); // Links the btnPlay variable to the layout component
        btnPause = findViewById(R.id.btnPause); // Links the btnPause variable to the layout component
        btnStop = findViewById(R.id.btnStop); // Links the btnStop variable to the layout component
        btnRestart = findViewById(R.id.btnRestart); // Links the btnRestart variable to the layout component
        
        btnPickVideo = findViewById(R.id.btnPickVideo); // Links the btnPickVideo variable to the layout component
        btnOpenFile = findViewById(R.id.btnOpenFile); // Links the btnOpenFile variable to the layout component
        btnOpenUrl = findViewById(R.id.btnOpenUrl); // Links the btnOpenUrl variable to the layout component
        
        // Initialize the media controller helper with the necessary UI components
        mediaControllerHelper = new MediaControllerHelper(videoView, progressBar, textStatus); // Creates a new instance of the helper

        // Set up click listeners for all buttons
        setupListeners(); // Calls the method to configure button clicks
    }

    /**
     * Configures the click event handlers for playback and selection buttons.
     */
    private void setupListeners() { // Method to define what happens when buttons are clicked
        // Playback control listeners delegated to the MediaControllerHelper
        btnPlay.setOnClickListener(v -> mediaControllerHelper.play()); // Sets click listener for Play button
        btnPause.setOnClickListener(v -> mediaControllerHelper.pause()); // Sets click listener for Pause button
        btnStop.setOnClickListener(v -> mediaControllerHelper.stop()); // Sets click listener for Stop button
        btnRestart.setOnClickListener(v -> mediaControllerHelper.restart()); // Sets click listener for Restart button
        
        // Listener to pick a video file from the device storage
        btnPickVideo.setOnClickListener(v -> { // Sets click listener for Pick Video button
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // Creates an intent to get content
            intent.setType("video/*"); // Filters content to video files only
            startActivityForResult(intent, REQUEST_PICK_VIDEO); // Starts the file picker activity
        });
        
        // Listener to open a document (supporting both audio and video)
        btnOpenFile.setOnClickListener(v -> { // Sets click listener for Open File button
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Creates an intent to open documents
            intent.addCategory(Intent.CATEGORY_OPENABLE); // Ensures the document can be opened
            intent.setType("*/*"); // Initial generic type filter
            String[] mimetypes = {"audio/*", "video/*"}; // Supported media types
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes); // Passes supported types to the intent
            startActivityForResult(intent, REQUEST_PICK_VIDEO); // Starts the document picker activity
        });
        
        // Listener to load and play media from a provided URL
        btnOpenUrl.setOnClickListener(v -> { // Sets click listener for Open URL button
            String url = editTextUrl.getText().toString().trim(); // Gets the URL from the input field
            if(!url.isEmpty()) { // Checks if the URL is not empty
                mediaControllerHelper.loadMedia(Uri.parse(url)); // Loads media from the parsed URL
                textFileName.setText(url); // Displays the URL as the file name
                videoView.setVisibility(View.VISIBLE); // Makes the video player visible
                findViewById(R.id.textVideoPlaceholder).setVisibility(View.GONE); // Hides the placeholder text
            }
        });
    }

    /**
     * Handles the result from the file picker or document opener.
     */
    @Override // Overrides the activity result handler
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // Called when a child activity finishes
        super.onActivityResult(requestCode, resultCode, data); // Calls super implementation
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) { // Checks if the result is from picking a video and was successful
            if (data != null && data.getData() != null) { // Ensures that data and URI are present
                Uri uri = data.getData(); // Gets the URI of the selected media
                // Load the selected media into the player
                mediaControllerHelper.loadMedia(uri); // Tells the helper to load the media from the URI
                // Update the UI to show the selected file name and reveal the video view
                textFileName.setText(uri.getLastPathSegment()); // Displays the file name part of the URI
                videoView.setVisibility(View.VISIBLE); // Shows the video player
                findViewById(R.id.textVideoPlaceholder).setVisibility(View.GONE); // Hides the placeholder
            }
        }
    }
}
