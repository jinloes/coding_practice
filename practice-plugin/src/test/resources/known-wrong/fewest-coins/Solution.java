package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minCoins(int[] coins, int amount) {
        int[] sorted = coins.clone();
        Arrays.sort(sorted);
        int count = 0;
        int remaining = amount;
        for (int i = sorted.length - 1; i >= 0; i--) {
            count += remaining / sorted[i];
            remaining %= sorted[i];
        }
        return remaining == 0 ? count : -1;
    }
}
