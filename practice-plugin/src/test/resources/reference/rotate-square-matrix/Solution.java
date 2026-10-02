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
        for (int[] row : matrix) {
            for (int left = 0, right = n - 1; left < right; left++, right--) {
                int swap = row[left];
                row[left] = row[right];
                row[right] = swap;
            }
        }
    }
}
