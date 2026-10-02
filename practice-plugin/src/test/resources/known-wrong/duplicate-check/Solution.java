package com.jinloes.practice;

public class Solution {
    public static boolean hasDuplicate(int[] numbers) {
        for (int i = 0; i + 1 < numbers.length; i++) {
            if (numbers[i] == numbers[i + 1]) {
                return true;
            }
        }
        return false;
    }
}
