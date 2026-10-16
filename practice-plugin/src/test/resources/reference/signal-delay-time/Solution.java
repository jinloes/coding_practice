package com.jinloes.practice;

import java.util.Arrays;
import java.util.PriorityQueue;

public class Solution {
    public static int signalDelay(int[][] links, int nodeCount, int source) {
        int[] head = new int[nodeCount + 1];
        int[] next = new int[links.length];
        Arrays.fill(head, -1);
        for (int i = 0; i < links.length; i++) {
            next[i] = head[links[i][0]];
            head[links[i][0]] = i;
        }
        int[] arrival = new int[nodeCount + 1];
        Arrays.fill(arrival, Integer.MAX_VALUE);
        arrival[source] = 0;
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        queue.add(new int[]{0, source});
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            if (top[0] > arrival[top[1]]) {
                continue;
            }
            for (int link = head[top[1]]; link >= 0; link = next[link]) {
                int target = links[link][1];
                int time = top[0] + links[link][2];
                if (time < arrival[target]) {
                    arrival[target] = time;
                    queue.add(new int[]{time, target});
                }
            }
        }
        int slowest = 0;
        for (int node = 1; node <= nodeCount; node++) {
            if (arrival[node] == Integer.MAX_VALUE) {
                return -1;
            }
            slowest = Math.max(slowest, arrival[node]);
        }
        return slowest;
    }
}
