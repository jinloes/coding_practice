package com.jinloes.practice;

public class Solution {
    public static long maxProfit(int[] payouts) {
        long skipped = 0, one = Long.MIN_VALUE / 4, two = Long.MIN_VALUE / 4;
        for (int payout : payouts) {
            long nextSkipped = Math.max(skipped, Math.max(one, two));
            long nextOne = skipped + payout;
            long nextTwo = one + payout;
            skipped = nextSkipped; one = nextOne; two = nextTwo;
        }
        return Math.max(skipped, Math.max(one, two));
    }
}
