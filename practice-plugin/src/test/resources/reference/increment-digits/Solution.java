package com.jinloes.practice;

public class Solution {
    public static int[] increment(int[] digits) {
        int[] result = digits.clone();
        for (int i = result.length - 1; i >= 0; i--) {
            if (result[i] < 9) {
                result[i]++;
                return result;
            }
            result[i] = 0;
        }
        int[] grown = new int[result.length + 1];
        grown[0] = 1;
        return grown;
    }
}
