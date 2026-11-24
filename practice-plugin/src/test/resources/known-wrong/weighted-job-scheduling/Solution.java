package com.jinloes.practice;

import java.util.Arrays;
import java.util.Comparator;

public class Solution {
    public int maxProfit(int[] startTimes, int[] endTimes, int[] profits) {
        int n = startTimes.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTimes[i];
            jobs[i][1] = endTimes[i];
            jobs[i][2] = profits[i];
        }
        // Bug: greedy by earliest end time ignores job profit.
        Arrays.sort(jobs, Comparator.comparingInt(job -> job[1]));
        int total = 0;
        int lastEnd = 0;
        for (int[] job : jobs) {
            if (job[0] >= lastEnd) {
                total += job[2];
                lastEnd = job[1];
            }
        }
        return total;
    }
}
