package com.jinloes.practice;

public class Solution {
    public static boolean canReach(int[] jumps) {
        int frontier = 0;
        for (int i = 0; i < jumps.length - 1; i++) {
            if (jumps[i] == 0 || i > frontier) {
                return false;
            }
            frontier = Math.max(frontier, i + jumps[i]);
        }
        return true;
    }
}
