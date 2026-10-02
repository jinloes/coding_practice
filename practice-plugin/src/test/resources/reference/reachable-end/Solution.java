package com.jinloes.practice;

public class Solution {
    public static boolean canReach(int[] jumps) {
        long frontier = 0;
        for (int i = 0; i < jumps.length; i++) {
            if (i > frontier) {
                return false;
            }
            frontier = Math.max(frontier, (long) i + jumps[i]);
            if (frontier >= jumps.length - 1) {
                return true;
            }
        }
        return true;
    }
}
