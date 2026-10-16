package com.jinloes.practice;

public class Solution {
    public static long countWays(int amount, int[] coins) {
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int total = 1; total <= amount; total++) {
            for (int coin : coins) {
                if (total >= coin) {
                    ways[total] += ways[total - coin];
                }
            }
        }
        return ways[amount];
    }
}
