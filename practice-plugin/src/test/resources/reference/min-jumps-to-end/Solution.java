package com.jinloes.practice;

public class Solution {
    public static int minJumps(int[] reach) {
        int hops = 0;
        int windowEnd = 0;
        int farthest = 0;
        for (int i = 0; i < reach.length - 1; i++) {
            farthest = Math.max(farthest, i + reach[i]);
            if (i == windowEnd) {
                hops++;
                windowEnd = farthest;
            }
        }
        return hops;
    }
}
