package com.jinloes.practice;

public class Solution {
    public static double power(double base, int exponent) {
        long remaining = Math.abs((long) exponent);
        double result = 1.0;
        double square = base;
        while (remaining > 0) {
            if ((remaining & 1) == 1) {
                result *= square;
            }
            square *= square;
            remaining >>= 1;
        }
        return exponent < 0 ? 1.0 / result : result;
    }
}
