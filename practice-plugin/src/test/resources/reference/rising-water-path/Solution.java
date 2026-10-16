package com.jinloes.practice;

import java.util.PriorityQueue;

public class Solution {
    private static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static int swimTime(int[][] elevation) {
        int rows = elevation.length;
        int cols = elevation[0].length;
        boolean[][] reached = new boolean[rows][cols];
        PriorityQueue<int[]> frontier = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        frontier.add(new int[]{elevation[0][0], 0, 0});
        reached[0][0] = true;
        int time = 0;
        while (!frontier.isEmpty()) {
            int[] cell = frontier.poll();
            time = Math.max(time, cell[0]);
            if (cell[1] == rows - 1 && cell[2] == cols - 1) {
                return time;
            }
            for (int[] step : STEPS) {
                int r = cell[1] + step[0];
                int c = cell[2] + step[1];
                if (r >= 0 && r < rows && c >= 0 && c < cols && !reached[r][c]) {
                    reached[r][c] = true;
                    frontier.add(new int[]{elevation[r][c], r, c});
                }
            }
        }
        return time;
    }
}
