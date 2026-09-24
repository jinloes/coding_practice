package com.jinloes.practice;

public class Solution {
    public static int missingNumber(int[] numbers) {
        int missing = numbers.length;
        for (int index = 0; index < numbers.length; index++) {
            missing ^= index ^ numbers[index];
        }
        return missing;
    }
}
