package com.jinloes.practice;

public class Solution {
    public static boolean isHappy(int n) {
        while (n >= 10) {
            int sum = 0;
            while (n > 0) {
                int digit = n % 10;
                sum += digit * digit;
                n /= 10;
            }
            n = sum;
        }
        return n == 1;
    }
}
