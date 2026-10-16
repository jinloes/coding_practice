package com.jinloes.practice;

import java.util.ArrayDeque;

public class Solution {
    private static final int[] DR = {1, -1, 0, 0};
    private static final int[] DC = {0, 0, 1, -1};

    public static int longestRise(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[] smallerNeighbours = new int[rows * cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int d = 0; d < 4; d++) {
                    int nr = r + DR[d];
                    int nc = c + DC[d];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] < grid[r][c]) {
                        smallerNeighbours[r * cols + c]++;
                    }
                }
            }
        }
        ArrayDeque<Integer> layer = new ArrayDeque<>();
        for (int cell = 0; cell < smallerNeighbours.length; cell++) {
            if (smallerNeighbours[cell] == 0) {
                layer.add(cell);
            }
        }
        int length = 0;
        while (!layer.isEmpty()) {
            length++;
            for (int count = layer.size(); count > 0; count--) {
                int cell = layer.poll();
                int r = cell / cols;
                int c = cell % cols;
                for (int d = 0; d < 4; d++) {
                    int nr = r + DR[d];
                    int nc = c + DC[d];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] > grid[r][c]
                            && --smallerNeighbours[nr * cols + nc] == 0) {
                        layer.add(nr * cols + nc);
                    }
                }
            }
        }
        return length;
    }
}
