package com.jinloes.practice;

import java.util.Arrays;
import java.util.Comparator;

public class Solution {
    public int maxProfit(int[] startTimes, int[] endTimes, int[] profits) {
        int n = startTimes.length;
        if (n == 0) {
            return 0;
        }
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTimes[i];
            jobs[i][1] = endTimes[i];
            jobs[i][2] = profits[i];
        }
        Arrays.sort(jobs, Comparator.comparingInt(job -> job[1]));

        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            ends[i] = jobs[i][1];
        }
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int start = jobs[i - 1][0];
            int profit = jobs[i - 1][2];
            int prev = upperBound(ends, i - 1, start);
            dp[i] = Math.max(dp[i - 1], dp[prev] + profit);
        }
        return dp[n];
    }

    private static int upperBound(int[] ends, int highExclusive, int target) {
        int low = 0;
        int high = highExclusive;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (ends[mid] <= target) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }
}
