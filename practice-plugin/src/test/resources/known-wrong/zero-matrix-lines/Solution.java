package com.jinloes.practice;

public class Solution {
    public static void zeroLines(int[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                if (matrix[r][c] == 0) {
                    for (int i = 0; i < matrix.length; i++) {
                        matrix[i][c] = 0;
                    }
                    for (int j = 0; j < matrix[r].length; j++) {
                        matrix[r][j] = 0;
                    }
                }
            }
        }
    }
}
