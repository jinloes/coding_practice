package com.jinloes.practice;

public class Solution {
    public static int startingStation(int[] gain, int[] cost) {
        long total = 0;
        long tank = 0;
        int start = 0;
        for (int i = 0; i < gain.length; i++) {
            long net = (long) gain[i] - cost[i];
            total += net;
            tank += net;
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        return total >= 0 ? start : -1;
    }
}
