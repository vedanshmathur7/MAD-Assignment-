package com.example.cameragalleryapp; // Package declaration for the app

import android.content.Context; // Import Context for system services
import android.database.Cursor; // Import Cursor for database queries
import android.net.Uri; // Import Uri for file locations
import android.provider.OpenableColumns; // Import columns for file metadata
import androidx.documentfile.provider.DocumentFile; // Import DocumentFile for abstraction

import java.text.SimpleDateFormat; // Import for date formatting
import java.util.Date; // Import Date class
import java.util.Locale; // Import Locale for regional formatting

/**
 * Utility class for handling file-related operations such as retrieving
 * file names, sizes, and modification dates from URIs.
 */
public class FileUtils { // Start of FileUtils class

    /**
     * Retrieves the display name of a file from its URI.
     */
    public static String getFileName(Context context, Uri uri) { // Method to get filename from Uri
        String result = null; // Initialize result as null
        // Check if the URI is a content URI (managed by a provider)
        if (uri.getScheme() != null && uri.getScheme().equals("content")) { // If scheme is 'content'
            // Query the content resolver for file metadata
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) { // Open cursor
                if (cursor != null && cursor.moveToFirst()) { // If cursor has data
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME); // Get column index for name
                    if (idx != -1) { // If column exists
                        result = cursor.getString(idx); // Get name from cursor
                    }
                }
            } // Cursor auto-closes here
        }
        // Fallback to path parsing if content resolver failed or it's a file URI
        if (result == null) { // If result is still null
            result = uri.getPath(); // Get raw path from Uri
            int cut = result.lastIndexOf('/'); // Find last slash position
            if (cut != -1) { // If slash found
                result = result.substring(cut + 1); // Extract everything after the slash
            }
        }
        return result; // Return the final filename
    }

    /**
     * Retrieves the size of a file in kilobytes (KB).
     */
    public static String getFileSize(Context context, Uri uri) { // Method to get file size
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri); // Wrap Uri in DocumentFile
        if (documentFile != null) { // If wrapper created successfully
            long sizeInBytes = documentFile.length(); // Get file size in bytes
            return (sizeInBytes / 1024) + " KB"; // Convert to KB and return as string
        }
        return "Unknown"; // Return 'Unknown' if file not found
    }

    /**
     * Retrieves the last modified date of a file.
     */
    public static String getFileDate(Context context, Uri uri) { // Method to get file date
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri); // Wrap Uri in DocumentFile
        if (documentFile != null) { // If wrapper created successfully
            long lastModified = documentFile.lastModified(); // Get timestamp in milliseconds
            // Format the timestamp into a readable date string and return it
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(lastModified));
        }
        return "Unknown"; // Return 'Unknown' if file not found
    }
}
