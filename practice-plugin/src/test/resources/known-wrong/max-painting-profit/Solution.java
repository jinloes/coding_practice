package com.jinloes.practice;

public class Solution {
    public static long maxProfit(int[] payouts) {
        long prevTwo = 0, prevOne = 0;
        for (int payout : payouts) { long current = Math.max(prevOne, prevTwo + payout); prevTwo = prevOne; prevOne = current; }
        return prevOne;
    }
}
