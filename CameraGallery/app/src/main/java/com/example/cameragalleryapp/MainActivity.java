package com.example.cameragalleryapp; // Package name for the application

import android.Manifest; // Import permissions for Manifest
import android.content.Intent; // Import Intent for activity communication
import android.content.SharedPreferences; // Import SharedPreferences for data storage
import android.content.pm.PackageManager; // Import for checking permissions
import android.net.Uri; // Import for handling file URIs
import android.os.Build; // Import for OS version checks
import android.os.Bundle; // Import for activity state saving
import android.provider.MediaStore; // Import for camera and gallery access
import android.widget.Button; // Import Button UI component
import android.widget.Toast; // Import Toast for short notifications

import androidx.annotation.NonNull; // Import for nullability check
import androidx.annotation.Nullable; // Import for nullability check
import androidx.appcompat.app.AppCompatActivity; // Import AppCompatActivity base class
import androidx.core.app.ActivityCompat; // Import for permission handling
import androidx.core.content.ContextCompat; // Import for permission and file provider
import androidx.core.content.FileProvider; // Import for secure file sharing
import androidx.documentfile.provider.DocumentFile; // Import for storage access abstraction
import androidx.recyclerview.widget.GridLayoutManager; // Import for grid layouts
import androidx.recyclerview.widget.RecyclerView; // Import for dynamic lists

import java.io.File; // Import File class
import java.io.InputStream; // Import for reading files
import java.io.OutputStream; // Import for writing files
import java.text.SimpleDateFormat; // Import for date formatting
import java.util.ArrayList; // Import for list implementation
import java.util.Date; // Import for date management
import java.util.List; // Import for list interface
import java.util.Locale; // Import for regional settings

public class MainActivity extends AppCompatActivity { // Definition of MainActivity class

    // Unique request codes to identify different callbacks
    private static final int REQ_PERMISSIONS = 100; // Code for permission requests
    private static final int REQ_CHOOSE_FOLDER = 101; // Code for folder picker
    private static final int REQ_TAKE_PHOTO = 102; // Code for camera request

    private Button btnTakePhoto, btnChooseFolder; // Button UI components
    private RecyclerView recyclerView; // List UI component
    private ImageAdapter imageAdapter; // Custom adapter for the image list
    private List<Uri> imageList = new ArrayList<>(); // List to hold image file locations
    
    private SharedPreferences sharedPrefs; // Local storage for app settings
    private Uri currentPhotoUri; // Temp URI for the photo being taken

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when activity starts
        super.onCreate(savedInstanceState); // Call base class method
        setContentView(R.layout.activity_main); // Set the layout file

        sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE); // Initialize shared preferences

        btnTakePhoto = findViewById(R.id.btnTakePhoto); // Bind Take Photo button from layout
        btnChooseFolder = findViewById(R.id.btnChooseFolder); // Bind Choose Folder button from layout
        recyclerView = findViewById(R.id.recyclerView); // Bind RecyclerView from layout

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // Display images in 3 columns
        imageAdapter = new ImageAdapter(this, imageList, uri -> { // Initialize image adapter with click listener
            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class); // Define intent for details
            intent.putExtra("image_uri", uri.toString()); // Pass URI to details activity
            startActivity(intent); // Open the details activity
        });
        recyclerView.setAdapter(imageAdapter); // Set the adapter to the RecyclerView

        btnTakePhoto.setOnClickListener(v -> checkPermissionsAndTakePhoto()); // Attach listener for take photo
        btnChooseFolder.setOnClickListener(v -> chooseFolder()); // Attach listener for choose folder
    }

    @Override
    protected void onResume() { // Called when returning to the activity
        super.onResume(); // Call base class method
        loadImages(); // Update the image list from the selected folder
    }

    private void chooseFolder() { // Method to launch the folder picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE); // Intent to select folder (SAF)
        startActivityForResult(intent, REQ_CHOOSE_FOLDER); // Start activity with request code
    }

    private void checkPermissionsAndTakePhoto() { // Logic for checking multiple permissions
        List<String> permissionsNeeded = new ArrayList<>(); // Dynamic list for missing permissions
        permissionsNeeded.add(Manifest.permission.CAMERA); // Always need camera
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // For Android 9 or lower
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE); // Need write access
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13 or higher
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES); // Use specific media permission
        } else { // For Android 10 to 12
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE); // Use generic storage permission
        }

        List<String> listPermissionsNeeded = new ArrayList<>(); // Store only the revoked permissions
        for (String p : permissionsNeeded) { // Loop through required permissions
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) { // If revoked
                listPermissionsNeeded.add(p); // Add to the prompt list
            }
        }

        if (!listPermissionsNeeded.isEmpty()) { // If any permission is missing
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQ_PERMISSIONS); // Ask user
        } else { // If all permissions are granted
            takePhoto(); // Launch camera
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // Handle user response to permission dialog
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call base class
        if (requestCode == REQ_PERMISSIONS) { // Check if response is for our request
            boolean allGranted = true; // Assume success
            for (int res : grantResults) { // Check every result
                if (res != PackageManager.PERMISSION_GRANTED) { // If any denied
                    allGranted = false; // Set failure
                    break; // Exit loop
                }
            }
            if (allGranted) { // If user clicked allow for all
                takePhoto(); // Launch camera
            } else { // If user denied
                Toast.makeText(this, "Permissions required to use this feature", Toast.LENGTH_SHORT).show(); // Notify user
            }
        }
    }

    private void takePhoto() { // Logic to prepare and launch camera
        String folderUriStr = sharedPrefs.getString("folder_uri", null); // Check if folder was picked
        if (folderUriStr == null) { // If no folder selected
            Toast.makeText(this, "Please select a folder first", Toast.LENGTH_SHORT).show(); // Error toast
            return; // Stop execution
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Create camera intent
        // Create unique filename based on current timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()); 
        File photoFile = new File(getCacheDir(), "IMG_" + timeStamp + ".jpg"); // Define temp file path in cache
        currentPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile); // Convert file to safe URI
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri); // Tell camera where to save the photo
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temp permission
        startActivityForResult(intent, REQ_TAKE_PHOTO); // Open camera app
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // Handle response from folder picker/camera
        super.onActivityResult(requestCode, resultCode, data); // Call base class
        if (requestCode == REQ_CHOOSE_FOLDER && resultCode == RESULT_OK && data != null) { // If folder picked successfully
            Uri treeUri = data.getData(); // Get the URI of the chosen folder
            if (treeUri != null) { // If URI is valid
                // Make permissions permanent so we don't lose access after app restart
                getContentResolver().takePersistableUriPermission(treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharedPrefs.edit().putString("folder_uri", treeUri.toString()).apply(); // Save URI to local storage
                loadImages(); // Update the list
            }
        } else if (requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK) { // If photo taken successfully
            savePhotoToSelectedFolder(); // Move image from temp cache to selected folder
            loadImages(); // Update the list
        }
    }

    private void savePhotoToSelectedFolder() { // Move image from internal cache to external folder
        try {
            String folderUriStr = sharedPrefs.getString("folder_uri", null); // Retrieve saved folder URI
            if (folderUriStr == null) return; // Stop if not set
            
            Uri treeUri = Uri.parse(folderUriStr); // Convert string to URI
            DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri); // Wrap folder URI for SAF
            if (folder == null || !folder.exists()) return; // Stop if folder invalid

            // Create final filename with timestamp
            String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            DocumentFile newFile = folder.createFile("image/jpeg", fileName); // Request to create file in folder
            if (newFile == null) return; // Stop if creation failed

            // Perform file data transfer using streams
            try (InputStream in = getContentResolver().openInputStream(currentPhotoUri); // Open temp image file
                 OutputStream out = getContentResolver().openOutputStream(newFile.getUri())) { // Open new target file
                if (in == null || out == null) return; // Stop if streams fail to open
                byte[] buffer = new byte[1024]; // Create small data buffer
                int len; // Variable for read length
                while ((len = in.read(buffer)) > 0) { // Read from source
                    out.write(buffer, 0, len); // Write to target
                }
            } // Streams auto-close
        } catch (Exception e) { // If anything goes wrong
            e.printStackTrace(); // Log error for debugging
        }
    }

    private void loadImages() { // Refresh the gallery list
        String folderUriStr = sharedPrefs.getString("folder_uri", null); // Retrieve saved folder URI
        if (folderUriStr == null) return; // Stop if not set

        Uri treeUri = Uri.parse(folderUriStr); // Convert string to URI
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri); // Wrap folder URI
        
        imageList.clear(); // Empty the current memory list
        if (folder != null && folder.exists() && folder.isDirectory()) { // Check if folder is valid
            for (DocumentFile file : folder.listFiles()) { // Iterate through all files in folder
                // Only accept common image formats
                if (file.isFile() && file.getType() != null && file.getType().startsWith("image/")) {
                    imageList.add(file.getUri()); // Add URI to the memory list
                }
            }
        }
        imageAdapter.notifyDataSetChanged(); // Tell RecyclerView to redraw the list
    }
}
