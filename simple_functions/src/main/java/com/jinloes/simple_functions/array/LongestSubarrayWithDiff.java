package com.jinloes.simple_functions.array;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Given an array of integers nums and an integer limit, return the size of the longest non-empty subarray such that
 * the absolute difference between any two elements of this subarray is less than or equal to limit.
 */
public class LongestSubarrayWithDiff {

    public int longestSubarray(int[] nums, int limit) {
        PriorityQueue<Integer> minSet = new PriorityQueue<>();
        PriorityQueue<Integer> maxSet = new PriorityQueue<>(Comparator.<Integer>naturalOrder().reversed());
        int count = 0;
        int leftIdx = 0;

        for (int i = 0; i < nums.length; i++) {
            // log(n)
            minSet.add(nums[i]);
            // log(n)
            maxSet.add(nums[i]);

            int min = minSet.peek();
            int max = maxSet.peek();

            int diff = Math.abs(min - max);
            if (diff <= limit) {
                count++;
            } else {
                // log(n)
                minSet.remove(nums[leftIdx]);
                // log(n)
                maxSet.remove(nums[leftIdx]);
                leftIdx++;
            }
        }
        return count;
    }
}