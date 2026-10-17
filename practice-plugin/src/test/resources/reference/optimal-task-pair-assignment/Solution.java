package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int[][] assignPairs(int[] durations) {
        int[] sorted = durations.clone();
        Arrays.sort(sorted);
        int[][] pairs = new int[sorted.length / 2][2];
        for (int i = 0; i < pairs.length; i++) { pairs[i][0] = sorted[i]; pairs[i][1] = sorted[sorted.length - 1 - i]; }
        return pairs;
    }
}
