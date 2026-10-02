package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minCoins(int[] coins, int amount) {
        int[] best = new int[amount + 1];
        Arrays.fill(best, amount + 1);
        best[0] = 0;
        for (int total = 1; total <= amount; total++) {
            for (int coin : coins) {
                if (coin <= total && best[total - coin] + 1 < best[total]) {
                    best[total] = best[total - coin] + 1;
                }
            }
        }
        return best[amount] > amount ? -1 : best[amount];
    }
}
