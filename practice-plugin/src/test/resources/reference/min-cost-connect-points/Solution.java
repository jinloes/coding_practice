package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minimumCost(int[][] points) {
        int count = points.length;
        if (count < 2) {
            return 0;
        }
        int[] cheapest = new int[count];
        boolean[] joined = new boolean[count];
        Arrays.fill(cheapest, Integer.MAX_VALUE);
        cheapest[0] = 0;
        int total = 0;
        for (int round = 0; round < count; round++) {
            int next = -1;
            for (int i = 0; i < count; i++) {
                if (!joined[i] && (next < 0 || cheapest[i] < cheapest[next])) {
                    next = i;
                }
            }
            joined[next] = true;
            total += cheapest[next];
            for (int i = 0; i < count; i++) {
                if (!joined[i]) {
                    int cost = Math.abs(points[i][0] - points[next][0]) + Math.abs(points[i][1] - points[next][1]);
                    cheapest[i] = Math.min(cheapest[i], cost);
                }
            }
        }
        return total;
    }
}
