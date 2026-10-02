package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int[][] merge(int[][] intervals) {
        if (intervals.length == 0) {
            return new int[0][];
        }
        int[][] sorted = intervals.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        int[][] merged = new int[sorted.length][];
        int count = 0;
        int start = sorted[0][0];
        int end = sorted[0][1];
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i][0] <= end) {
                end = Math.max(end, sorted[i][1]);
            } else {
                merged[count++] = new int[]{start, end};
                start = sorted[i][0];
                end = sorted[i][1];
            }
        }
        merged[count++] = new int[]{start, end};
        return Arrays.copyOf(merged, count);
    }
}
