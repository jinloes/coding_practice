package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<int[]> drainageCells(int[][] heights) {
        List<int[]> result = new ArrayList<>();
        int rows = heights.length;
        if (rows == 0 || heights[0].length == 0) {
            return result;
        }
        int cols = heights[0].length;
        boolean[] west = new boolean[rows * cols];
        boolean[] east = new boolean[rows * cols];
        int[] stack = new int[rows * cols];
        for (int r = 0; r < rows; r++) {
            fill(heights, west, stack, r, 0);
            fill(heights, east, stack, r, cols - 1);
        }
        for (int c = 0; c < cols; c++) {
            fill(heights, west, stack, 0, c);
            fill(heights, east, stack, rows - 1, c);
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (west[r * cols + c] && east[r * cols + c]) {
                    result.add(new int[]{r, c});
                }
            }
        }
        return result;
    }

    private static void fill(int[][] heights, boolean[] seen, int[] stack, int startRow, int startCol) {
        int rows = heights.length;
        int cols = heights[0].length;
        if (seen[startRow * cols + startCol]) {
            return;
        }
        int top = 0;
        seen[startRow * cols + startCol] = true;
        stack[top++] = startRow * cols + startCol;
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        while (top > 0) {
            int cell = stack[--top];
            int r = cell / cols;
            int c = cell % cols;
            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];
                if (nr < 0 || nc < 0 || nr >= rows || nc >= cols || seen[nr * cols + nc]) {
                    continue;
                }
                if (heights[nr][nc] >= heights[r][c]) {
                    seen[nr * cols + nc] = true;
                    stack[top++] = nr * cols + nc;
                }
            }
        }
    }
}
