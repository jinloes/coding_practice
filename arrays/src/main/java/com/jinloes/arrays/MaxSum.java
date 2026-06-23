package com.jinloes.arrays;

/**
 * Finds the max sum in an array.
 */
public class MaxSum {
    public static int findMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int maxEndingHere = arr[0];
        int maxSoFar = arr[0];
        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }

    /**
     * Finds the max sum in a 2d array where you can only go right or down.
     *
     * @param arr array calculate the max sum
     * @return max sum
     */
    public static int findMax(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0].length == 0) {
            return 0;
        }

        int rows = arr.length;
        int cols = arr[0].length;
        int[][] maxArr = new int[rows][cols];
        maxArr[0][0] = arr[0][0];

        for (int col = 1; col < cols; col++) {
            maxArr[0][col] = maxArr[0][col - 1] + arr[0][col];
        }

        for (int row = 1; row < rows; row++) {
            maxArr[row][0] = maxArr[row - 1][0] + arr[row][0];
        }

        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                maxArr[row][col] = Math.max(maxArr[row - 1][col], maxArr[row][col - 1]) + arr[row][col];
            }
        }

        return maxArr[rows - 1][cols - 1];
    }
}
