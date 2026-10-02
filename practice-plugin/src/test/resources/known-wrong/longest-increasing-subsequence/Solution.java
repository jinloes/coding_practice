package com.jinloes.practice;

public class Solution {
    public static int lengthOfLis(int[] numbers) {
        int[] tails = new int[numbers.length];
        int length = 0;
        for (int value : numbers) {
            int low = 0;
            int high = length;
            while (low < high) {
                int middle = (low + high) >>> 1;
                if (tails[middle] <= value) {
                    low = middle + 1;
                } else {
                    high = middle;
                }
            }
            tails[low] = value;
            if (low == length) {
                length++;
            }
        }
        return length;
    }
}
