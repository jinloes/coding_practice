package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    private static final int OPEN = Integer.MAX_VALUE;

    public static void fillDistances(int[][] rooms) {
        for (int r = 0; r < rooms.length; r++) {
            for (int c = 0; c < rooms[r].length; c++) {
                if (rooms[r][c] == 0) {
                    spread(rooms, r, c);
                }
            }
        }
    }

    private static void spread(int[][] rooms, int startR, int startC) {
        boolean[][] seen = new boolean[rooms.length][rooms[0].length];
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startR, startC, 0});
        seen[startR][startC] = true;
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int d = 0; d < 4; d++) {
                int r = cur[0] + dr[d];
                int c = cur[1] + dc[d];
                if (r < 0 || c < 0 || r >= rooms.length || c >= rooms[0].length || seen[r][c]
                        || rooms[r][c] == -1 || rooms[r][c] == 0) {
                    continue;
                }
                seen[r][c] = true;
                rooms[r][c] = cur[2] + 1;
                queue.add(new int[]{r, c, cur[2] + 1});
            }
        }
    }
}
