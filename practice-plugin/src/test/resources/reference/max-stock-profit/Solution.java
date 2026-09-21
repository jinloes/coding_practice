package com.jinloes.practice;

public class Solution {
    public static int maxProfit(int[] prices) {
        int best = 0;
        int cheapest = Integer.MAX_VALUE;
        for (int price : prices) {
            if (price < cheapest) {
                cheapest = price;
            } else if (price - cheapest > best) {
                best = price - cheapest;
            }
        }
        return best;
    }
}
