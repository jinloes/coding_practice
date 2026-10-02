package com.jinloes.practice;

public class Solution {
    public static void rotateClockwise(int[][] matrix) {
        int n = matrix.length;
        for (int r = 0; r < n; r++) {
            for (int c = r + 1; c < n; c++) {
                int swap = matrix[r][c];
                matrix[r][c] = matrix[c][r];
                matrix[c][r] = swap;
            }
        }
        for (int top = 0, bottom = n - 1; top < bottom; top++, bottom--) {
            int[] swap = matrix[top];
            matrix[top] = matrix[bottom];
            matrix[bottom] = swap;
        }
    }
}
