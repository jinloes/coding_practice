package com.jinloes.practice;

public class Solution {
    public static int startingStation(int[] gain, int[] cost) {
        long total = 0;
        for (int i = 0; i < gain.length; i++) {
            total += (long) gain[i] - cost[i];
        }
        return total >= 0 ? 0 : -1;
    }
}
