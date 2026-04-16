package com.example.currencyconverter; // The folder this file lives in

import java.util.Locale; // Used for formatting numbers based on country rules

/**
 * A helper class that handles the math for currency conversion.
 */
public class CurrencyConverter {

    // These are our fixed conversion rates (Relative to 1 INR)
    private static final double USD_TO_INR = 83.0; // 1 USD = 83 INR
    private static final double EUR_TO_INR = 90.0; // 1 EUR = 90 INR
    private static final double JPY_TO_INR = 0.55; // 1 JPY = 0.55 INR
    private static final double INR_TO_INR = 1.0;  // 1 INR = 1 INR (Base)

    /**
     * Converts an amount from one currency to another using INR as a middle-man.
     */
    public static double convert(double amount, String from, String to) {
        // Step 1: Convert the starting amount into INR
        double amountInINR = convertToINR(amount, from);
        
        // Step 2: Convert that INR amount into the target currency
        return convertFromINR(amountInINR, to);
    }

    /**
     * Multiplies the amount by the rate to get the value in INR.
     */
    private static double convertToINR(double amount, String currency) {
        switch (currency) {
            case "USD": return amount * USD_TO_INR; // Multiply USD by 83
            case "EUR": return amount * EUR_TO_INR; // Multiply EUR by 90
            case "JPY": return amount * JPY_TO_INR; // Multiply JPY by 0.55
            default:    return amount; // If it's already INR, just return the amount
        }
    }

    /**
     * Divides the INR value by the rate to get the final currency value.
     */
    private static double convertFromINR(double amountInINR, String currency) {
        switch (currency) {
            case "USD": return amountInINR / USD_TO_INR; // Divide INR by 83 to get USD
            case "EUR": return amountInINR / EUR_TO_INR; // Divide INR by 90 to get EUR
            case "JPY": return amountInINR / JPY_TO_INR; // Divide INR by 0.55 to get JPY
            default:    return amountInINR; // If target is INR, keep it as is
        }
    }

    /**
     * Makes the final number look clean (e.g., 83.0000).
     */
    public static String formatResult(double result, String currencyCode) {
        // Format to 4 decimal places and add the currency code (like "USD") at the end
        return String.format(Locale.getDefault(), "%.4f %s", result, currencyCode);
    }
}
