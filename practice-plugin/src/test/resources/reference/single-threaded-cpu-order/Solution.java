package com.jinloes.practice;

import java.util.Arrays;
import java.util.PriorityQueue;

public class Solution {
    public static int[] processingOrder(int[][] tasks) {
        int n = tasks.length;
        int[][] indexed = new int[n][3];
        for (int i = 0; i < n; i++) indexed[i] = new int[]{tasks[i][0], tasks[i][1], i};
        Arrays.sort(indexed, (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[2], b[2]));
        PriorityQueue<int[]> ready = new PriorityQueue<>((a, b) -> a[1] != b[1] ? Integer.compare(a[1], b[1]) : Integer.compare(a[2], b[2]));
        int[] order = new int[n]; long time = 0; int next = 0;
        for (int out = 0; out < n; out++) {
            if (ready.isEmpty() && time < indexed[next][0]) time = indexed[next][0];
            while (next < n && indexed[next][0] <= time) ready.add(indexed[next++]);
            int[] task = ready.remove(); order[out] = task[2]; time += task[1];
        }
        return order;
    }
}
