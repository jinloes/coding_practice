package com.jinloes.simple_functions.array;

import java.util.Collections;
import java.util.PriorityQueue;

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
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        int count = 0;
        int left = 0;

        for (int num : nums) {
            // Expand the window by adding the new element to both heaps — O(log n)
            minHeap.add(num);
            maxHeap.add(num);

            // Check if the window is valid: max - min <= limit
            if (maxHeap.peek() - minHeap.peek() <= limit) {
                count++;
            } else {
                // Window is invalid; shrink from the left to restore the constraint — O(n) worst case
                minHeap.remove(nums[left]);
                maxHeap.remove(nums[left]);
                left++;
            }
        }
        return count;
    }
}