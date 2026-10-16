package com.jinloes.practice;

import java.util.PriorityQueue;

public class Solution {
    public static int findKthLargest(int[] nums, int k) {
        if (k < 1 || k > nums.length) {
            throw new IllegalArgumentException("k must be between 1 and " + nums.length);
        }
        PriorityQueue<Integer> topK = new PriorityQueue<>();
        for (int value : nums) {
            if (topK.size() < k) {
                topK.add(value);
            } else if (value > topK.peek()) {
                topK.poll();
                topK.add(value);
            }
        }
        return topK.peek();
    }
}
