package com.jinloes.practice;

public class Solution {
    public static int[] productExceptSelf(int[] numbers) {
        int total = 1;
        for (int value : numbers) {
            total *= value;
        }
        int[] result = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            result[i] = total / numbers[i];
        }
        return result;
    }
}
