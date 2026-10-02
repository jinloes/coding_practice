package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>(intervals.length + 1);
        int start = newInterval[0];
        int end = newInterval[1];
        int i = 0;
        while (i < intervals.length && intervals[i][1] <= start) {
            result.add(new int[]{intervals[i][0], intervals[i][1]});
            i++;
        }
        while (i < intervals.length && intervals[i][0] < end) {
            start = Math.min(start, intervals[i][0]);
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        result.add(new int[]{start, end});
        while (i < intervals.length) {
            result.add(new int[]{intervals[i][0], intervals[i][1]});
            i++;
        }
        return result.toArray(new int[0][]);
    }
}
