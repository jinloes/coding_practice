package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Solution {
    public static int shortestPath(int[][] grid, int eliminations) {
        int rows = grid.length;
        int cols = grid[0].length;
        if (rows == 1 && cols == 1) return 0;
        int[][] best = new int[rows][cols];
        for (int[] row : best) Arrays.fill(row, -1);
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0, eliminations, 0});
        best[0][0] = eliminations;
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        while (!queue.isEmpty()) {
            int[] state = queue.remove();
            for (int d = 0; d < 4; d++) {
                int nr = state[0] + dr[d];
                int nc = state[1] + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                int nextBudget = state[2] - grid[nr][nc];
                if (nextBudget < 0 || nextBudget <= best[nr][nc]) continue;
                int nextDistance = state[3] + 1;
                if (nr == rows - 1 && nc == cols - 1) return nextDistance;
                best[nr][nc] = nextBudget;
                queue.add(new int[]{nr, nc, nextBudget, nextDistance});
            }
        }
        return -1;
    }
}
