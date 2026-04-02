package com.example.currencyconverter;

/**
 * CurrencyConverter - Handles all currency conversion logic.
 *
 * Fixed conversion rates (all relative to INR as base currency):
 *   1 INR  = 1.0    INR
 *   1 USD  = 83.0   INR
 *   1 JPY  = 1/1.8  INR  (i.e., 1 INR = 1.8 JPY)
 *   1 EUR  = 90.0   INR
 */
public class CurrencyConverter {

    // Rate: 1 unit of each currency expressed in INR
    private static final double INR_RATE = 1.0;
    private static final double USD_RATE = 83.0;
    private static final double JPY_RATE = 1.0 / 1.8;   // ≈ 0.5556 INR per JPY
    private static final double EUR_RATE = 90.0;

    /**
     * Returns the value of 1 unit of the given currency in INR.
     *
     * @param currency Currency code (INR, USD, JPY, EUR)
     * @return Equivalent value in INR; returns 1.0 for unknown codes
     */
    private static double toINRRate(String currency) {
        switch (currency) {
            case "INR": return INR_RATE;
            case "USD": return USD_RATE;
            case "JPY": return JPY_RATE;
            case "EUR": return EUR_RATE;
            default:    return 1.0;
        }
    }

    /**
     * Converts an amount from one currency to another.
     *
     * Strategy: source → INR → target (two-step via base currency).
     *
     * @param amount       The amount to convert (must be > 0)
     * @param fromCurrency Source currency code
     * @param toCurrency   Target currency code
     * @return Converted amount in target currency
     * @throws IllegalArgumentException if amount is negative
     */
    public static double convert(double amount, String fromCurrency, String toCurrency) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        // Same currency — no conversion needed
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        // Step 1: convert source currency to INR
        double amountInINR = amount * toINRRate(fromCurrency);

        // Step 2: convert INR to target currency
        return amountInINR / toINRRate(toCurrency);
    }

    /**
     * Formats the conversion result as a readable string.
     *
     * @param result       Numeric result of conversion
     * @param toCurrency   Target currency code
     * @return Formatted result string (e.g., "Result: 83.00 INR")
     */
    public static String formatResult(double result, String toCurrency) {
        return String.format("%.4f %s", result, toCurrency);
    }
}
