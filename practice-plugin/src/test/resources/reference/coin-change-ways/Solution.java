package com.jinloes.practice;

public class Solution {
    public static long countWays(int amount, int[] coins) {
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int coin : coins) {
            for (int total = coin; total <= amount; total++) {
                ways[total] += ways[total - coin];
            }
        }
        return ways[amount];
    }
}
