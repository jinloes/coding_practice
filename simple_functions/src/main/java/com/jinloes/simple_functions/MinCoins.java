package com.jinloes.simple_functions;

import java.util.HashMap;
import java.util.Map;

/**
 * Finds the minimum number of coins to equal a sum.
 */
public class MinCoins {
    public static int findMinCoins(int sum, int[] coinValues) {
        Map<Integer, Integer> minCoins = new HashMap<>();
        minCoins.put(0, 0);
        for (int i = 1; i <= sum; i++) {
            int closestCoin = findClosestCoin(i, coinValues);
            if (i - closestCoin > 0) {
                minCoins.put(i, minCoins.get(closestCoin) + minCoins.get(i - closestCoin));
            } else {
                minCoins.put(i, 1);
            }
        }
        return minCoins.get(sum);
    }

    private static int findClosestCoin(int value, int[] coinValues) {
        int closestCoin = 0;
        for (int i = 0; i < coinValues.length; i++) {
            if (value - coinValues[i] >= 0) {
                closestCoin = coinValues[i];
            }
        }
        return closestCoin;
    }

    public static int findMinCoinsAlternative(int sum, int[] coinValues) {
        Map<Integer, Integer> sumMap = new HashMap<>();
        sumMap.put(0, 0);
        // Until we reach the sum
        for (int i = 0; i <= sum; i++) {
            // add each coin value to previously known sum and see if it takes less coins to achieve than currently
            // known
            if (sumMap.containsKey(i)) {
                for (int value : coinValues) {
                    int foundSum = i + value;
                    int numCoins = sumMap.get(i) + 1;
                    if (!sumMap.containsKey(foundSum) || sumMap.get(foundSum) > numCoins) {
                        sumMap.put(foundSum, numCoins);
                    }
                }
            } else {
                sumMap.put(i, 0);
            }
        }
        return sumMap.get(sum);
    }
}
