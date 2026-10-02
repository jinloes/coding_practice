package com.jinloes.practice;

public class Solution {
    public static int[] spiralOrder(int[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            return new int[0];
        }
        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;
        int[] result = new int[matrix.length * matrix[0].length];
        int next = 0;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) {
                result[next++] = matrix[top][c];
            }
            top++;
            for (int r = top; r <= bottom; r++) {
                result[next++] = matrix[r][right];
            }
            right--;
            {
                for (int c = right; c >= left; c--) {
                    result[next++] = matrix[bottom][c];
                }
                bottom--;
            }
            {
                for (int r = bottom; r >= top; r--) {
                    result[next++] = matrix[r][left];
                }
                left++;
            }
        }
        return result;
    }
}
