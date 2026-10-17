package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static long minimumTotalWaitTime(int[] serviceTimes) {
        int[] sorted = serviceTimes.clone();
        Arrays.sort(sorted);
        long total = 0;
        for (int i = 0; i < sorted.length; i++) total += (long) sorted[i] * (sorted.length - i - 1);
        return total;
    }
}
