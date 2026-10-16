package com.jinloes.practice;

public class Solution {
    public static int minimumCost(int[][] points) {
        int count = points.length;
        boolean[] visited = new boolean[count];
        int total = 0;
        int current = 0;
        for (int step = 1; step < count; step++) {
            visited[current] = true;
            int best = -1;
            int bestCost = Integer.MAX_VALUE;
            for (int i = 0; i < count; i++) {
                int cost = Math.abs(points[i][0] - points[current][0]) + Math.abs(points[i][1] - points[current][1]);
                if (!visited[i] && cost < bestCost) {
                    bestCost = cost;
                    best = i;
                }
            }
            total += bestCost;
            current = best;
        }
        return total;
    }
}
