package com.jinloes.practice;

public class Solution {
    public static int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE, best = 0;
        for (int price : prices) { min = Math.min(min, price); best = Math.max(best, price - min); }
        return best;
    }
}
