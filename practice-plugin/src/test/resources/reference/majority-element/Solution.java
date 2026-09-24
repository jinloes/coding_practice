package com.jinloes.practice;

public class Solution {
    public static int majorityElement(int[] numbers) {
        int candidate = numbers[0];
        int count = 0;
        for (int value : numbers) {
            if (count == 0) {
                candidate = value;
            }
            count += value == candidate ? 1 : -1;
        }
        return candidate;
    }
}
