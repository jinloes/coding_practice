package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int findUnpaired(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i] != sorted[i + 1]) {
                return sorted[i];
            }
        }
        return sorted[sorted.length - 1];
    }
}
