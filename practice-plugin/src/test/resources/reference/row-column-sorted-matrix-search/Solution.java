package com.jinloes.practice;

public class Solution {
    public static boolean contains(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) return false;
        int row = 0, col = matrix[0].length - 1;
        while (row < matrix.length && col >= 0) {
            int value = matrix[row][col];
            if (value == target) return true;
            if (value < target) row++; else col--;
        }
        return false;
    }
}
