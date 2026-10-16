package com.jinloes.practice;

public class Solution {
    private static final int OPEN = Integer.MAX_VALUE;

    public static void fillDistances(int[][] rooms) {
        int rows = rooms.length;
        if (rows == 0) {
            return;
        }
        int cols = rooms[0].length;
        int[] queue = new int[rows * cols];
        int head = 0;
        int tail = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (rooms[r][c] == 0) {
                    queue[tail++] = r * cols + c;
                }
            }
        }
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        while (head < tail) {
            int cell = queue[head++];
            int base = rooms[cell / cols][cell % cols];
            for (int d = 0; d < 4; d++) {
                int r = cell / cols + dr[d];
                int c = cell % cols + dc[d];
                if (r < 0 || c < 0 || r >= rows || c >= cols || rooms[r][c] != OPEN) {
                    continue;
                }
                rooms[r][c] = base + 1;
                queue[tail++] = r * cols + c;
            }
        }
    }
}
