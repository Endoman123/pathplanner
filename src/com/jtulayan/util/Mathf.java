package com.jtulayan.util;

/**
 * Collection of math functions
 */
public class Mathf {
    public static final double
        FT_TO_METERS = 0.3048,
        METERS_TO_FT = 1.0 / FT_TO_METERS;

    private Mathf() {
        // Do not instantiate
    }

    /**
     * Rounds the specified value to the specified number of decimal places
     *
     * @param val    the number to round
     * @param places the amonut of decimal places to round to
     * @return number rounded to places
     */
    public static double round(double val, int places) {
        double tens = Math.pow(10.0, places);

        return Math.round(val * tens) / tens;
    }

    /**
     * Rounds the specified value to the nearest specified multiple
     *
     * @param val    the number to round
     * @param multiple the multiple to round val to
     * @return number rounded to nearest multiple
     */
    public static double round(double val, double multiple) {
        return Math.round(val / multiple) * multiple;
    }
}
