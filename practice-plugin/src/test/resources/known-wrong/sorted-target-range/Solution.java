package com.jinloes.practice;

public class Solution {
    public static int[] targetRange(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) if (numbers[i] == target) return new int[]{i, i};
        return new int[]{-1, -1};
    }
}
