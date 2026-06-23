package com.jinloes.arrays;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * You are given an array of integers nums, there is a sliding window of size k which is moving from the very left
 * of the array to the very right. You can only see the k numbers in the window. Each time the sliding window moves right by one position.
 * <p>
 * Return the max sliding window.
 */
public class MaxSlidingWindow {

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        k = Math.min(k, nums.length);
        Deque<Integer> window = new ArrayDeque<>();
        int[] maxArr = new int[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {
            while (!window.isEmpty() && window.peekFirst() <= i - k) {
                window.removeFirst();
            }

            while (!window.isEmpty() && nums[window.peekLast()] <= nums[i]) {
                window.removeLast();
            }

            window.addLast(i);

            if (i >= k - 1) {
                maxArr[i - k + 1] = nums[window.peekFirst()];
            }
        }

        return maxArr;
    }
}
