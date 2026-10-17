package com.jinloes.practice;

public class Solution {
    public static int maximumScore(int[] nums, int[] multipliers) {
        int m = multipliers.length;
        int[] next = new int[m + 1];
        for (int op = m - 1; op >= 0; op--) {
            int[] current = new int[op + 1];
            for (int left = op; left >= 0; left--) {
                int right = nums.length - 1 - (op - left);
                current[left] = Math.max(multipliers[op] * nums[left] + next[left + 1], multipliers[op] * nums[right] + next[left]);
            }
            next = current;
        }
        return next[0];
    }
}
