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
            if (r > 0 && image[r - 1][c] == original) {
                image[r - 1][c] = color;
                stack[top++] = cell - cols;
            }
            if (r + 1 < rows && image[r + 1][c] == original) {
                image[r + 1][c] = color;
                stack[top++] = cell + cols;
            }
            if (c > 0 && image[r][c - 1] == original) {
                image[r][c - 1] = color;
                stack[top++] = cell - 1;
            }
            if (c + 1 < cols && image[r][c + 1] == original) {
                image[r][c + 1] = color;
                stack[top++] = cell + 1;
            }
        }
        return image;
    }
}
