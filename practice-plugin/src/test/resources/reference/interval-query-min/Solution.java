package com.jinloes.practice;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Solution {
    public static int[] smallestCovering(int[][] intervals, int[] queries) {
        int[][] byStart = intervals.clone();
        Arrays.sort(byStart, Comparator.comparingInt(interval -> interval[0]));
        Integer[] order = new Integer[queries.length];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        Arrays.sort(order, Comparator.comparingInt(i -> queries[i]));

        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.comparingInt(entry -> entry[0]));
        int[] answers = new int[queries.length];
        int next = 0;
        for (int index : order) {
            int point = queries[index];
            while (next < byStart.length && byStart[next][0] <= point) {
                open.add(new int[]{byStart[next][1] - byStart[next][0] + 1, byStart[next][1]});
                next++;
            }
            while (!open.isEmpty() && open.peek()[1] < point) {
                open.poll();
            }
            answers[index] = open.isEmpty() ? -1 : open.peek()[0];
        }
        return answers;
    }
}
