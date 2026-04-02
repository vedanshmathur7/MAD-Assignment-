package com.example.cameragalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSIONS = 100;
    private static final int REQ_CHOOSE_FOLDER = 101;
    private static final int REQ_TAKE_PHOTO = 102;

    private Button btnTakePhoto, btnChooseFolder;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imageList = new ArrayList<>();
    
    private SharedPreferences sharedPrefs;
    private Uri currentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChooseFolder = findViewById(R.id.btnChooseFolder);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this, imageList, uri -> {
            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            intent.putExtra("image_uri", uri.toString());
            startActivity(intent);
        });
        recyclerView.setAdapter(imageAdapter);

        btnTakePhoto.setOnClickListener(v -> checkPermissionsAndTakePhoto());
        btnChooseFolder.setOnClickListener(v -> chooseFolder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    private void chooseFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQ_CHOOSE_FOLDER);
    }

    private void checkPermissionsAndTakePhoto() {
        List<String> permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionsNeeded) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQ_PERMISSIONS);
        } else {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSIONS) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                takePhoto();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePhoto() {
        String folderUriStr = sharedPrefs.getString("folder_uri", null);
        if (folderUriStr == null) {
            Toast.makeText(this, "Please select a folder first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(getCacheDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg");
        currentPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQ_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CHOOSE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                getContentResolver().takePersistableUriPermission(treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharedPrefs.edit().putString("folder_uri", treeUri.toString()).apply();
                loadImages();
            }
        } else if (requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK) {
            savePhotoToSelectedFolder();
            loadImages();
        }
    }

    private void savePhotoToSelectedFolder() {
        try {
            String folderUriStr = sharedPrefs.getString("folder_uri", null);
            if (folderUriStr == null) return;
            
            Uri treeUri = Uri.parse(folderUriStr);
            DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);
            if (folder == null || !folder.exists()) return;

            String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
            DocumentFile newFile = folder.createFile("image/jpeg", fileName);
            if (newFile == null) return;

            try (InputStream in = getContentResolver().openInputStream(currentPhotoUri);
                 OutputStream out = getContentResolver().openOutputStream(newFile.getUri())) {
                if (in == null || out == null) return;
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        String folderUriStr = sharedPrefs.getString("folder_uri", null);
        if (folderUriStr == null) return;

        Uri treeUri = Uri.parse(folderUriStr);
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);
        
        imageList.clear();
        if (folder != null && folder.exists() && folder.isDirectory()) {
            for (DocumentFile file : folder.listFiles()) {
                if (file.isFile() && file.getType() != null && file.getType().startsWith("image/")) {
                    imageList.add(file.getUri());
                }
            }
        }
        imageAdapter.notifyDataSetChanged();
    }
}
