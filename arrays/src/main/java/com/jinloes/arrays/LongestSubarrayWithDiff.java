package com.jinloes.arrays;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Given an array of integers nums and an integer limit, return the size of the longest non-empty subarray such that
 * the absolute difference between any two elements of this subarray is less than or equal to limit.
 *
 * <p>Approach: sliding window with a min-heap and max-heap to track the current window's min/max.
 * The window expands by one each iteration; if the constraint is violated, the left element is
 * evicted so the window size stays the same. This guarantees count equals the longest valid window.
 *
 * <p>Time complexity: O(n log n) — each element is added/removed from each heap at most once,
 * and heap add/remove are O(log n).
 * Space complexity: O(n) — both heaps hold at most n elements.
 */
public class LongestSubarrayWithDiff {

    public int longestSubarray(int[] nums, int limit) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        Deque<Integer> minDeque = new ArrayDeque<>();
        Deque<Integer> maxDeque = new ArrayDeque<>();
        int longest = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            int num = nums[right];

            while (!minDeque.isEmpty() && minDeque.peekLast() > num) {
                minDeque.removeLast();
            }
            minDeque.addLast(num);

            while (!maxDeque.isEmpty() && maxDeque.peekLast() < num) {
                maxDeque.removeLast();
            }
            maxDeque.addLast(num);

            while (maxDeque.peekFirst() - minDeque.peekFirst() > limit) {
                int leftValue = nums[left];
                if (leftValue == minDeque.peekFirst()) {
                    minDeque.removeFirst();
                }
                if (leftValue == maxDeque.peekFirst()) {
                    maxDeque.removeFirst();
                }
                left++;
            }

            longest = Math.max(longest, right - left + 1);
        }

        return longest;
    }
}
