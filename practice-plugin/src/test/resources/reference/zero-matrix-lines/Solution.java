package com.jinloes.practice;

public class Solution {
    public static void zeroLines(int[][] matrix) {
        int rows = matrix.length;
        if (rows == 0 || matrix[0].length == 0) {
            return;
        }
        int cols = matrix[0].length;
        boolean firstRowHasZero = false;
        boolean firstColHasZero = false;
        for (int c = 0; c < cols; c++) {
            if (matrix[0][c] == 0) {
                firstRowHasZero = true;
            }
        }
        for (int r = 0; r < rows; r++) {
            if (matrix[r][0] == 0) {
                firstColHasZero = true;
            }
        }
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (matrix[r][c] == 0) {
                    matrix[r][0] = 0;
                    matrix[0][c] = 0;
                }
            }
        }
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) {
                    matrix[r][c] = 0;
                }
            }
        }
        if (firstRowHasZero) {
            for (int c = 0; c < cols; c++) {
                matrix[0][c] = 0;
            }
        }
        if (firstColHasZero) {
            for (int r = 0; r < rows; r++) {
                matrix[r][0] = 0;
            }
        }
    }
}
