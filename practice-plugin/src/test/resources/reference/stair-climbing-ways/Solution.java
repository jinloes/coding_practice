package com.jinloes.practice;

public class Solution {
    public static long countWays(int steps) {
        long previous = 1;
        long current = 1;
        for (int stair = 2; stair <= steps; stair++) {
            long next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }
}
