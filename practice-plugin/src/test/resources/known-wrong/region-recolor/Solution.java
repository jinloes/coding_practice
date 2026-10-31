package com.jinloes.practice;

public class Solution {
    public static int[][] recolor(int[][] image, int row, int col, int color) {
        int original = image[row][col];
        if (original == color) {
            return image;
        }
        int rows = image.length;
        int cols = image[0].length;
        int[] stack = new int[rows * cols];
        int top = 0;
        image[row][col] = color;
        stack[top++] = row * cols + col;
        while (top > 0) {
            int cell = stack[--top];
            int r = cell / cols;
            int c = cell % cols;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == original) {
                        image[nr][nc] = color;
                        stack[top++] = nr * cols + nc;
                    }
                }
            }
        }
        return image;
    }
}
