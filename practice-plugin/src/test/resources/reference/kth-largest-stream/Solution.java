package com.jinloes.practice;

import java.util.PriorityQueue;

public class Solution {
    private final int k;
    private final PriorityQueue<Integer> topK = new PriorityQueue<>();

    public Solution(int k, int[] initial) {
        if (k < 1 || initial.length < k - 1) {
            throw new IllegalArgumentException("Need k >= 1 and at least k - 1 starting values");
        }
        this.k = k;
        for (int value : initial) {
            offer(value);
        }
    }

    public int add(int value) {
        offer(value);
        return topK.peek();
    }

    private void offer(int value) {
        if (topK.size() < k) {
            topK.add(value);
        } else if (value > topK.peek()) {
            topK.poll();
            topK.add(value);
        }
    }
}
