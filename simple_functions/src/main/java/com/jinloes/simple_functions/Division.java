package com.jinloes.simple_functions;

/**
 * Takes two numbers. A numerator and denominator and returns result with n decimal places.
 */
public class Division {
    public static String divide(int numerator, int denominator, int decimalPlaces) {
        float result = (float) numerator / (float) denominator;
        return String.format("%." + decimalPlaces + "f", result);
    }
}
