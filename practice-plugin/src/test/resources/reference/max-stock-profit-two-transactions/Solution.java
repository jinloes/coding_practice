package com.jinloes.practice;

public class Solution {
    public static int maxProfit(int[] prices) {
        if (prices.length < 2) return 0;
        int n = prices.length;
        int[] left = new int[n];
        int min = prices[0];
        for (int i = 1; i < n; i++) { min = Math.min(min, prices[i]); left[i] = Math.max(left[i - 1], prices[i] - min); }
        int[] right = new int[n];
        int max = prices[n - 1];
        for (int i = n - 2; i >= 0; i--) { max = Math.max(max, prices[i]); right[i] = Math.max(right[i + 1], max - prices[i]); }
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, left[i] + right[i]);
        return best;
    }
}
