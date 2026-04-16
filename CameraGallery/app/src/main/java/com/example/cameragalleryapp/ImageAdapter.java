package com.example.cameragalleryapp; // Package declaration

import android.content.Context; // Context for layout inflation
import android.net.Uri; // Uri to handle image sources
import android.view.LayoutInflater; // For converting XML to View objects
import android.view.View; // Base class for UI components
import android.view.ViewGroup; // Container for layout views
import android.widget.ImageView; // UI component for showing images

import androidx.annotation.NonNull; // To mark parameters as non-null
import androidx.recyclerview.widget.RecyclerView; // Base class for dynamic lists

import java.util.List; // For handling image URI lists

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> { // Define the adapter

    private Context context; // Reference to context
    private List<Uri> imageUris; // List of image locations
    private OnImageClickListener listener; // Custom click listener callback

    public interface OnImageClickListener { // Define custom interface
        void onImageClick(Uri uri); // Callback method for image click
    }

    public ImageAdapter(Context context, List<Uri> imageUris, OnImageClickListener listener) { // Constructor
        this.context = context; // Initialize context
        this.imageUris = imageUris; // Initialize URI list
        this.listener = listener; // Initialize click listener
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Create a new row/item
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false); // Inflate the XML
        return new ImageViewHolder(view); // Wrap the view in a ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) { // Fill item with data
        Uri uri = imageUris.get(position); // Get URI for current position
        holder.imageView.setImageURI(null); // Clear memory to avoid showing old images during scroll
        holder.imageView.setImageURI(uri); // Set the new image URI to the ImageView
        holder.itemView.setOnClickListener(v -> listener.onImageClick(uri)); // Handle clicks on the whole item
    }

    @Override
    public int getItemCount() { // Get total number of items in list
        return imageUris.size(); // Return size of the URI list
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder { // Inner class to hold UI references
        ImageView imageView; // Reference to the image view component

        public ImageViewHolder(@NonNull View itemView) { // Constructor for ViewHolder
            super(itemView); // Call parent constructor
            imageView = itemView.findViewById(R.id.imageViewThumbnail); // Find image view by ID
        }
    }
}
