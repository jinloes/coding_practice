package com.jinloes.practice;

public class Solution {
    public static int[] productExceptSelf(int[] numbers) {
        int[] result = new int[numbers.length];
        int prefix = 1;
        for (int i = 0; i < numbers.length; i++) {
            result[i] = prefix;
            prefix *= numbers[i];
        }
        int suffix = 1;
        for (int i = numbers.length - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= numbers[i];
        }
        return result;
    }
}
