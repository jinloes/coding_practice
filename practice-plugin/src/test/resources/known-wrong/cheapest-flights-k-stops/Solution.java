package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int cheapestPrice(int cityCount, int[][] flights, int source, int target, int maxStops) {
        int unreachable = Integer.MAX_VALUE;
        int[] cost = new int[cityCount];
        Arrays.fill(cost, unreachable);
        cost[source] = 0;
        for (int leg = 0; leg <= maxStops && leg < cityCount; leg++) {
            int[] next = cost;
            for (int[] flight : flights) {
                if (cost[flight[0]] != unreachable && cost[flight[0]] + flight[2] < next[flight[1]]) {
                    next[flight[1]] = cost[flight[0]] + flight[2];
                }
            }
            cost = next;
        }
        return cost[target] == unreachable ? -1 : cost[target];
    }
}
