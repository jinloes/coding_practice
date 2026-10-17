package com.jinloes.practice;

public class Solution {
    public static int longestZigZag(int[] numbers) {
        if (numbers.length == 0) return 0;
        int up = 1, down = 1;
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > numbers[i - 1]) up = down + 1;
            else if (numbers[i] < numbers[i - 1]) down = up + 1;
        }
        return Math.max(up, down);
    }
}
