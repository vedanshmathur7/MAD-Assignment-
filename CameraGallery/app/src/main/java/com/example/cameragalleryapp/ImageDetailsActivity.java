package com.example.cameragalleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class ImageDetailsActivity extends AppCompatActivity {

    private ImageView imageViewFull;
    private TextView tvName, tvPath, tvSize, tvDate;
    private Button btnDelete;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        imageViewFull = findViewById(R.id.imageViewFull); 
        tvName = findViewById(R.id.tvName); 
        tvPath = findViewById(R.id.tvPath); 
        tvSize = findViewById(R.id.tvSize); 
        tvDate = findViewById(R.id.tvDate); 
        btnDelete = findViewById(R.id.btnDelete); 

        String uriStr = getIntent().getStringExtra("image_uri"); 
        if (uriStr != null) {
            imageUri = Uri.parse(uriStr);
            loadImageDetails();
        }

        btnDelete.setOnClickListener(v -> showDeleteConfirmation()); 
    }

    private void loadImageDetails() { 
        imageViewFull.setImageURI(imageUri); 
        tvName.setText("Name: " + FileUtils.getFileName(this, imageUri)); 
        tvPath.setText("Path: " + imageUri.getPath()); 
        tvSize.setText("Size: " + FileUtils.getFileSize(this, imageUri)); 
        tvDate.setText("Date: " + FileUtils.getFileDate(this, imageUri)); 
    }

    private void showDeleteConfirmation() { 
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", (dialog, which) -> deleteImage()) 
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteImage() { 
        try {
            DocumentFile file = DocumentFile.fromSingleUri(this, imageUri); 
            if (file != null && file.delete()) { 
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else { 
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
