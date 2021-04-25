package com.jinloes.simple_functions.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

/**
 * Given an infinite number of quarters, dimes, nickels, and pennies, calculate the number of ways to represent n cents.
 */
public class Coins {
    private Map<Integer, Integer> memo = new HashMap<>();

    public int numWays(int n) {
        if (n == 0) {
            return 1;
        } else if (n < 0) {
            return 0;
        }

        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        int num = numWays(n - 25) + numWays(n - 10) + numWays(n - 5) + numWays(n - 1);
        memo.put(n, num);
        return num;
    }
}
