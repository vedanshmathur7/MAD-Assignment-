package com.example.currencyconverter; // Folder location of the file

import android.content.SharedPreferences; // Used to save settings
import android.os.Bundle; // Holds screen state
import android.view.View; // Base class for UI items
import android.widget.CompoundButton; // Handles toggle switch events

import androidx.activity.EdgeToEdge; // Full screen support
import androidx.appcompat.app.AppCompatActivity; // Standard screen base class
import androidx.appcompat.app.AppCompatDelegate; // Controls Dark/Light mode
import androidx.appcompat.widget.Toolbar; // The top title bar
import androidx.core.graphics.Insets; // Represents system bar areas
import androidx.core.view.ViewCompat; // Padding helper
import androidx.core.view.WindowInsetsCompat; // System bar information

import com.example.currencyconverterapp.R; // Link to resource IDs
import com.google.android.material.switchmaterial.SwitchMaterial; // Modern toggle switch

public class SettingsActivity extends AppCompatActivity { // Code for our Settings Screen

    private SwitchMaterial switchDarkMode; // Variable for the toggle switch
    private static final String PREFS_NAME    = "CurrencyConverterPrefs"; // Saved file name
    private static final String KEY_DARK_MODE = "DarkMode"; // ID for the dark mode setting

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when screen opens
        EdgeToEdge.enable(this); // Allow drawing behind status bar
        
        super.onCreate(savedInstanceState); // Standard startup
        setContentView(R.layout.activity_settings); // Link to XML layout

        // Prevent UI from hiding under the status bar
        View settingsView = findViewById(R.id.settings_layout); // Find root layout
        if (settingsView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(settingsView, (v, insets) -> { // Listen for screen edges
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // Get bar sizes
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Add padding
                return insets; // Finish
            });
        }

        // Setup the top bar with a back button
        Toolbar toolbar = findViewById(R.id.toolbar); // Find toolbar
        setSupportActionBar(toolbar); // Set it as the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the back arrow
            getSupportActionBar().setTitle(R.string.title_settings); // Set title to "Settings"
        }

        switchDarkMode = findViewById(R.id.switchDarkMode); // Connect Java to XML switch
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Open saved file
        switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, false)); // Set switch to saved state

        // What happens when you toggle the switch
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { // Runs when toggled
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit(); // Prepare to save
                editor.putBoolean(KEY_DARK_MODE, isChecked); // Put new setting in
                editor.apply(); // Save it permanently

                // Change the app's look immediately
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Dark Mode ON
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Dark Mode OFF
                }
                recreate(); // Refresh the screen to show colors changing
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() { // When the top-left back arrow is clicked
        onBackPressed(); // Go back to the previous screen
        return true; // Finished
    }
}
