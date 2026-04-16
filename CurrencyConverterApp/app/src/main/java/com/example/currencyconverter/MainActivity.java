package com.example.currencyconverter; // Tells the computer which folder this file belongs to

import android.content.Intent; // Used to move from one screen to another
import android.content.SharedPreferences; // Used to save small data like Dark Mode setting
import android.os.Bundle; // Holds the app state when starting
import android.text.TextUtils; // Helps check if a piece of text is empty
import android.view.Menu; // Used to create the top-right menu
import android.view.MenuItem; // Used to handle clicks on menu items
import android.view.View; // Base class for all UI parts (buttons, text)
import android.widget.ArrayAdapter; // Bridges data (list) and the UI (Spinner)
import android.widget.Spinner; // A drop-down menu for selecting items
import android.widget.TextView; // Used to display text on screen
import android.widget.Toast; // Shows small pop-up messages

import androidx.activity.EdgeToEdge; // Allows the app to use the full screen area
import androidx.appcompat.app.AppCompatActivity; // The base class for modern Android screens
import androidx.appcompat.app.AppCompatDelegate; // Helps control the app's theme (Dark/Light)
import androidx.appcompat.widget.Toolbar; // The top bar with the app name
import androidx.core.graphics.Insets; // Represents screen areas like status bar
import androidx.core.view.ViewCompat; // Helps with screen padding on different Android versions
import androidx.core.view.WindowInsetsCompat; // Provides info about system bars

import com.example.currencyconverterapp.R; // Link to all resource IDs (layouts, strings)
import com.google.android.material.button.MaterialButton; // A fancy modern button
import com.google.android.material.card.MaterialCardView; // A fancy box with shadow
import com.google.android.material.textfield.TextInputEditText; // A modern text input box
import com.google.android.material.textfield.TextInputLayout; // Adds floating labels to input boxes

public class MainActivity extends AppCompatActivity { // The code for our Main Screen

    // We create "variables" here to represent parts of our UI
    private TextInputEditText etAmount; // The box where you type the number
    private TextInputLayout tilAmount; // The container for the input box (handles errors)
    private Spinner spinnerFrom; // The first drop-down menu (From Currency)
    private Spinner spinnerTo; // The second drop-down menu (To Currency)
    private MaterialButton btnConvert; // The "Convert" button
    private TextView tvResult; // The text that shows the answer
    private TextView tvResultLabel; // The text that shows "100 USD ="
    private MaterialCardView cardResult; // The box that pops up with the answer

    // Constants for saving settings (like an ID card for our preferences)
    private static final String PREFS_NAME    = "CurrencyConverterPrefs"; // Name of our storage file
    private static final String KEY_DARK_MODE = "DarkMode"; // Key for the Dark Mode setting

    @Override
    protected void onCreate(Bundle savedInstanceState) { // This runs when the screen starts
        applyPersistedTheme(); // Check if user wants Dark or Light mode first
        
        EdgeToEdge.enable(this); // Make the app fill the whole screen (including status bar)

        super.onCreate(savedInstanceState); // Do the standard startup things
        setContentView(R.layout.activity_main); // Link this code to our XML layout file

        // This part makes sure our UI doesn't hide under the status bar
        View mainView = findViewById(R.id.main_layout); // Find the root layout from XML
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> { // Listen for screen edges
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // Get bar sizes
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Add padding
                return insets; // Finish setting up padding
            });
        }

        // Set up the top title bar
        Toolbar toolbar = findViewById(R.id.toolbar); // Find the toolbar in XML
        setSupportActionBar(toolbar); // Tell the app to use it as the main Action Bar

        // Connect our Java variables to the items in our XML file
        tilAmount    = findViewById(R.id.tilAmount); // Connect the input container
        etAmount     = findViewById(R.id.etAmount); // Connect the input box
        spinnerFrom  = findViewById(R.id.spinnerFrom); // Connect the first drop-down
        spinnerTo    = findViewById(R.id.spinnerTo); // Connect the second drop-down
        btnConvert   = findViewById(R.id.btnConvert); // Connect the button
        tvResult     = findViewById(R.id.tvResult); // Connect the result text
        tvResultLabel = findViewById(R.id.tvResultLabel); // Connect the result label
        cardResult   = findViewById(R.id.cardResult); // Connect the result card

        setupCurrencySpinners(); // Fill the drop-down menus with currency names

        // Tell the button what to do when someone taps it
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // This runs when the button is clicked
                performConversion(); // Start the math and show the answer
            }
        });
    }

    private void setupCurrencySpinners() { // Fills the drop-down menus
        String[] currencies = {"INR", "USD", "JPY", "EUR"}; // Our list of currencies
        // Create an "Adapter" to show our list inside the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Style the drop-down
        spinnerFrom.setAdapter(adapter); // Put the list in the first Spinner
        spinnerTo.setAdapter(adapter); // Put the list in the second Spinner
        spinnerFrom.setSelection(1); // Start with "USD" selected
        spinnerTo.setSelection(0);   // Start with "INR" selected
    }

    private void performConversion() { // The "Brains" of the conversion
        tilAmount.setError(null); // Remove any old error messages
        // Get the text from the input box
        String amountStr = (etAmount.getText() != null) ? etAmount.getText().toString().trim() : "";

        if (TextUtils.isEmpty(amountStr)) { // If the user didn't type anything
            tilAmount.setError(getString(R.string.error_enter_amount)); // Show a "Required" error
            return; // Stop here
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr); // Turn the text "100" into a real number
        } catch (NumberFormatException e) { // If it's not a valid number
            tilAmount.setError(getString(R.string.error_invalid_input)); // Show an error
            return; // Stop here
        }

        if (amount < 0) { // If the number is negative
            tilAmount.setError(getString(R.string.error_invalid_input)); // Show an error
            return; // Stop here
        }

        // Get the names of the selected currencies
        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency   = spinnerTo.getSelectedItem().toString();

        // Use our helper class to do the math
        double result = CurrencyConverter.convert(amount, fromCurrency, toCurrency);
        String resultText = CurrencyConverter.formatResult(result, toCurrency); // Format the number nicely
        
        // Update the screen with the answer
        tvResultLabel.setText(String.format("%s %s  =", amountStr, fromCurrency)); // Show e.g., "100 USD ="
        tvResult.setText(resultText); // Show the final answer
        cardResult.setVisibility(View.VISIBLE); // Make the result box appear
    }

    private void applyPersistedTheme() { // Applies the saved theme (Dark or Light)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Open saved file
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false); // Get the setting
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Turn on Dark Mode
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Turn on Light Mode
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Creates the menu button
        getMenuInflater().inflate(R.menu.menu_main, menu); // Show the menu from our XML file
        return true; // Finished
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // What to do when a menu item is clicked
        if (item.getItemId() == R.id.action_settings) { // If "Settings" was clicked
            startActivity(new Intent(MainActivity.this, SettingsActivity.class)); // Open the Settings screen
            return true; // Finished
        }
        return super.onOptionsItemSelected(item); // Do standard menu things
    }
}
