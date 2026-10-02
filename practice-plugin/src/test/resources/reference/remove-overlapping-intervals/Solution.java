package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minRemovals(int[][] intervals) {
        if (intervals.length == 0) {
            return 0;
        }
        int[][] sorted = intervals.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[1], b[1]));
        int kept = 1;
        int lastEnd = sorted[0][1];
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i][0] >= lastEnd) {
                kept++;
                lastEnd = sorted[i][1];
            }
        }
        return sorted.length - kept;
    }
}
