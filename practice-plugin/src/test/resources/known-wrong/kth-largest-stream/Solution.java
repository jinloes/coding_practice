package com.jinloes.practice;

import java.util.TreeSet;

public class Solution {
    private final int k;
    private final TreeSet<Integer> topK = new TreeSet<>();

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
        return topK.first();
    }

    private void offer(int value) {
        topK.add(value);
        if (topK.size() > k) {
            topK.pollFirst();
        }
    }
}
