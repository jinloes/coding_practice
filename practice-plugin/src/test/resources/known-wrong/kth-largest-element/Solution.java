package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int findKthLargest(int[] nums, int k) {
        if (k < 1 || k > nums.length) {
            throw new IllegalArgumentException("k must be between 1 and " + nums.length);
        }
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        return sorted[k - 1];
    }
}
