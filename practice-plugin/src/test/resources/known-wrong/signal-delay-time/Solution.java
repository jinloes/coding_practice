package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Arrays;

public class Solution {
    public static int signalDelay(int[][] links, int nodeCount, int source) {
        int[] arrival = new int[nodeCount + 1];
        Arrays.fill(arrival, -1);
        arrival[source] = 0;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            int node = queue.remove();
            for (int[] link : links) {
                if (link[0] == node && arrival[link[1]] < 0) {
                    arrival[link[1]] = arrival[node] + link[2];
                    queue.add(link[1]);
                }
            }
        }
        int slowest = 0;
        for (int node = 1; node <= nodeCount; node++) {
            if (arrival[node] < 0) {
                return -1;
            }
            slowest = Math.max(slowest, arrival[node]);
        }
        return slowest;
    }
}
