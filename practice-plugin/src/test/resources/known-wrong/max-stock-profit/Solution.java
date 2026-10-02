package com.jinloes.practice;
public class Solution {
    public static int maxProfit(int[] prices) {
        return prices.length < 2 ? 0 : prices[prices.length - 1] - prices[0];
    }
}
