package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.currencyconverterapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etAmount;
    private TextInputLayout tilAmount;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private MaterialButton btnConvert;
    private TextView tvResult;
    private TextView tvResultLabel;
    private MaterialCardView cardResult;

    // Constants for SharedPreferences storage
    private static final String PREFS_NAME    = "CurrencyConverterPrefs";
    private static final String KEY_DARK_MODE = "DarkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preference before creating the view
        applyPersistedTheme();
        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adjust padding for system bars (status/navigation bars)
        View mainView = findViewById(R.id.main_layout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        tilAmount    = findViewById(R.id.tilAmount);
        etAmount     = findViewById(R.id.etAmount);
        spinnerFrom  = findViewById(R.id.spinnerFrom);
        spinnerTo    = findViewById(R.id.spinnerTo);
        btnConvert   = findViewById(R.id.btnConvert);
        tvResult     = findViewById(R.id.tvResult);
        tvResultLabel = findViewById(R.id.tvResultLabel);
        cardResult   = findViewById(R.id.cardResult);

        setupCurrencySpinners();

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void setupCurrencySpinners() {
        String[] currencies = {"INR", "USD", "JPY", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setSelection(1); // Default to USD
        spinnerTo.setSelection(0);   // Default to INR
    }

    private void performConversion() {
        tilAmount.setError(null);
        String amountStr = (etAmount.getText() != null) ? etAmount.getText().toString().trim() : "";

        // Validation checks
        if (TextUtils.isEmpty(amountStr)) {
            tilAmount.setError(getString(R.string.error_enter_amount));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            tilAmount.setError(getString(R.string.error_invalid_input));
            return;
        }

        if (amount < 0) {
            tilAmount.setError(getString(R.string.error_invalid_input));
            return;
        }

        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency   = spinnerTo.getSelectedItem().toString();

        // Perform calculation and update UI
        double result = CurrencyConverter.convert(amount, fromCurrency, toCurrency);
        String resultText = CurrencyConverter.formatResult(result, toCurrency);
        
        tvResultLabel.setText(String.format("%s %s  =", amountStr, fromCurrency));
        tvResult.setText(resultText);
        cardResult.setVisibility(View.VISIBLE);
    }

    private void applyPersistedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
