package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static boolean canAttendAll(int[][] meetings) {
        int[][] sorted = meetings.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i][0] < sorted[i - 1][1]) {
                return false;
            }
        }
        return true;
    }
}
