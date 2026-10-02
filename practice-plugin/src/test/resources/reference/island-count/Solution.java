package com.jinloes.practice;

public class Solution {
    public static int countIslands(char[][] grid) {
        int rows = grid.length;
        int count = 0;
        int[] stack = new int[0];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != '1') {
                    continue;
                }
                count++;
                int cols = grid[r].length;
                if (stack.length < rows * cols) {
                    stack = new int[rows * cols];
                }
                int top = 0;
                grid[r][c] = '0';
                stack[top++] = r * cols + c;
                while (top > 0) {
                    int cell = stack[--top];
                    int cr = cell / cols;
                    int cc = cell % cols;
                    if (cr > 0 && grid[cr - 1][cc] == '1') {
                        grid[cr - 1][cc] = '0';
                        stack[top++] = (cr - 1) * cols + cc;
                    }
                    if (cr + 1 < rows && grid[cr + 1][cc] == '1') {
                        grid[cr + 1][cc] = '0';
                        stack[top++] = (cr + 1) * cols + cc;
                    }
                    if (cc > 0 && grid[cr][cc - 1] == '1') {
                        grid[cr][cc - 1] = '0';
                        stack[top++] = cr * cols + cc - 1;
                    }
                    if (cc + 1 < cols && grid[cr][cc + 1] == '1') {
                        grid[cr][cc + 1] = '0';
                        stack[top++] = cr * cols + cc + 1;
                    }
                }
            }
        }
        return count;
    }
}
