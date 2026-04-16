package com.example.cameragalleryapp; // Package declaration for the app

import android.content.Intent; // Import Intent for activity communication
import android.net.Uri; // Import Uri for handling file locations
import android.os.Bundle; // Import Bundle for state management
import android.widget.Button; // Import Button UI component
import android.widget.ImageView; // Import ImageView UI component
import android.widget.TextView; // Import TextView UI component
import android.widget.Toast; // Import Toast for short notifications

import androidx.appcompat.app.AlertDialog; // Import AlertDialog for confirmation dialogs
import androidx.appcompat.app.AppCompatActivity; // Import AppCompatActivity base class
import androidx.documentfile.provider.DocumentFile; // Import DocumentFile for file operations

public class ImageDetailsActivity extends AppCompatActivity { // Start of ImageDetailsActivity class

    private ImageView imageViewFull; // Declaration of ImageView for the full image
    private TextView tvName, tvPath, tvSize, tvDate; // Declaration of TextViews for metadata
    private Button btnDelete; // Declaration of Button for deletion
    private Uri imageUri; // Uri to store the current image's location

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Method called when activity is created
        super.onCreate(savedInstanceState); // Call the parent class's onCreate method
        setContentView(R.layout.activity_image_details); // Set the layout file for this activity

        imageViewFull = findViewById(R.id.imageViewFull); // Link the ImageView from layout by ID
        tvName = findViewById(R.id.tvName); // Link the name TextView by ID
        tvPath = findViewById(R.id.tvPath); // Link the path TextView by ID
        tvSize = findViewById(R.id.tvSize); // Link the size TextView by ID
        tvDate = findViewById(R.id.tvDate); // Link the date TextView by ID
        btnDelete = findViewById(R.id.btnDelete); // Link the delete Button by ID

        String uriStr = getIntent().getStringExtra("image_uri"); // Retrieve the image URI string passed from MainActivity
        if (uriStr != null) { // Check if the URI string is not null
            imageUri = Uri.parse(uriStr); // Parse the string into a Uri object
            loadImageDetails(); // Call method to display image and its details
        }

        btnDelete.setOnClickListener(v -> showDeleteConfirmation()); // Set up click listener for the delete button
    }

    private void loadImageDetails() { // Method to populate the UI with image information
        imageViewFull.setImageURI(imageUri); // Load the image from URI into the ImageView
        tvName.setText("Name: " + FileUtils.getFileName(this, imageUri)); // Set name using FileUtils helper
        tvPath.setText("Path: " + imageUri.getPath()); // Set the raw path of the image URI
        tvSize.setText("Size: " + FileUtils.getFileSize(this, imageUri)); // Set file size using FileUtils helper
        tvDate.setText("Date: " + FileUtils.getFileDate(this, imageUri)); // Set modification date using FileUtils helper
    }

    private void showDeleteConfirmation() { // Method to show a "Are you sure?" dialog
        new AlertDialog.Builder(this) // Create a new dialog builder
                .setTitle("Delete Image") // Set the dialog title
                .setMessage("Are you sure you want to delete this image?") // Set the dialog message
                .setPositiveButton("YES", (dialog, which) -> deleteImage()) // Action to take if user clicks 'YES'
                .setNegativeButton("NO", null) // Do nothing if user clicks 'NO'
                .show(); // Display the dialog to the user
    }

    private void deleteImage() { // Method to perform the actual file deletion
        try {
            DocumentFile file = DocumentFile.fromSingleUri(this, imageUri); // Wrap the URI in a DocumentFile
            if (file != null && file.delete()) { // If the file exists and is successfully deleted
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show(); // Show success message
                finish(); // Close this activity and return to the gallery
            } else { // If deletion fails
                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show(); // Show failure message
            }
        } catch (Exception e) { // Catch any unexpected errors
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show(); // Show error message
        }
    }
}
