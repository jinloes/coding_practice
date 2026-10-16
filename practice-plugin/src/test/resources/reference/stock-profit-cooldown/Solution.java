package com.jinloes.practice;

public class Solution {
    public static int maxProfit(int[] prices) {
        int holding = Integer.MIN_VALUE / 2;
        int justSold = 0;
        int resting = 0;
        for (int price : prices) {
            int nextHolding = Math.max(holding, resting - price);
            int nextSold = holding + price;
            int nextResting = Math.max(resting, justSold);
            holding = nextHolding;
            justSold = nextSold;
            resting = nextResting;
        }
        return Math.max(justSold, resting);
    }
}
