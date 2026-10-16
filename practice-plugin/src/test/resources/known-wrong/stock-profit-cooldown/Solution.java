package com.jinloes.practice;

public class Solution {
    public static int maxProfit(int[] prices) {
        int holding = Integer.MIN_VALUE / 2;
        int resting = 0;
        for (int price : prices) {
            int nextHolding = Math.max(holding, resting - price);
            int nextResting = Math.max(resting, holding + price);
            holding = nextHolding;
            resting = nextResting;
        }
        return resting;
    }
}
