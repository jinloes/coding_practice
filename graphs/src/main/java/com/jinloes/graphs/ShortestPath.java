package com.jinloes.graphs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

class ShortestPath {
    private static final int[][] DIRECTIONS = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};


    public int shortestPath(int[][] grid, int k) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0, 0, 0});
        Set<String> visited = new HashSet<>();
        visited.add("0,0,0");


        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            int steps = current[2];
            int objRemoved = current[3];

            if (x == grid.length - 1 && y == grid[0].length - 1) {
                return steps;
            }

            for (int[] direction : DIRECTIONS) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                String key = x + "," + y + "," + objRemoved;

                if (isValidSpace(newX, newY, grid)
                        && !visited.contains(key)
                        && (grid[newX][newY] == 0
                        || (grid[newX][newY] == 1 && objRemoved < k))) {
                    if (grid[newX][newY] == 1) {
                        objRemoved++;
                    }
                    queue.add(new int[]{newX, newY, steps + 1, objRemoved});
                    visited.add(key);
                }
            }
        }

        return -1;
    }

    private boolean isValidSpace(int x, int y, int[][] grid) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }
}
