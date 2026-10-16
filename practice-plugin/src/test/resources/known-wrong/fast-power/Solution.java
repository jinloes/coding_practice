package com.jinloes.practice;

public class Solution {
    public static double power(double base, int exponent) {
        double result = 1.0;
        double square = base;
        int remaining = exponent;
        if (remaining < 0) {
            square = 1.0 / square;
            remaining = -remaining;
        }
        while (remaining > 0) {
            if ((remaining & 1) == 1) {
                result *= square;
            }
            square *= square;
            remaining >>= 1;
        }
        return result;
    }
}
