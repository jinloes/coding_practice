package com.jinloes.practice;

public class Solution {
    public static int longestZigZag(int[] numbers) {
        if (numbers.length == 0) return 0;
        int length = 1;
        for (int i = 1; i < numbers.length; i++) if (numbers[i] != numbers[i - 1]) length++;
        return length;
    }
}
