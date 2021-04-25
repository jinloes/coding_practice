package com.jinloes.simple_functions.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

/**
 * You are given two integer arrays nums and multipliers of size n and m respectively, where n >= m.
 * The arrays are 1-indexed.
 * <p>
 * You begin with a score of 0. You want to perform exactly m operations. On the ith operation (1-indexed), you will:
 * <p>
 * Choose one integer x from either the start or the end of the array nums.
 * Add multipliers[i] * x to your score.
 * Remove x from the array nums.
 * Return the maximum score after performing m operations.
 */
public class MaxScoreMultiOperations {

    public int maximumScore(int[] nums, int[] multipliers) {
        Map<String, Integer> memo = new HashMap<>();
        return maxScore(nums, multipliers, 0, nums.length - 1, 0, memo);
    }

    public int maxScore(int[] nums, int[] multipliers, int start, int end, int i, Map<String, Integer> memo) {
        if (i == multipliers.length) {
            return 0;
        }
        String key = start + ":" + end;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int sumStart = multipliers[i] * nums[start] + maxScore(nums, multipliers, start + 1, end, i + 1, memo);
        int sumEnd = multipliers[i] * nums[end] + maxScore(nums, multipliers, start, end - 1, i + 1, memo);

        int max = Math.max(sumStart, sumEnd);
        memo.put(key, max);
        return max;
    }
}
