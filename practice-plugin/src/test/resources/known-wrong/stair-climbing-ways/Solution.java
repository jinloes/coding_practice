package com.jinloes.practice;

public class Solution {
    public static long countWays(int steps) {
        int previous = 1;
        int current = 1;
        for (int stair = 2; stair <= steps; stair++) {
            int next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }
}
