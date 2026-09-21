package com.jinloes.practice;

public class Solution {
    public static void moveZeroes(int[] numbers) {
        int write = 0;
        for (int number : numbers) {
            if (number != 0) {
                numbers[write++] = number;
            }
        }
        while (write < numbers.length) {
            numbers[write++] = 0;
        }
    }
}
