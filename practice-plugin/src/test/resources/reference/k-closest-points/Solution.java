package com.jinloes.practice;

import java.util.PriorityQueue;

public class Solution {
    public static int[][] closest(int[][] points, int k) {
        if (k < 0 || k > points.length) {
            throw new IllegalArgumentException("k must be between 0 and " + points.length);
        }
        PriorityQueue<int[]> farthestFirst = new PriorityQueue<>((a, b) -> Long.compare(squared(b), squared(a)));
        for (int[] point : points) {
            if (farthestFirst.size() < k) {
                farthestFirst.add(point);
            } else if (k > 0 && squared(point) < squared(farthestFirst.peek())) {
                farthestFirst.poll();
                farthestFirst.add(point);
            }
        }
        return farthestFirst.toArray(new int[0][]);
    }

    private static long squared(int[] point) {
        return (long) point[0] * point[0] + (long) point[1] * point[1];
    }
}
