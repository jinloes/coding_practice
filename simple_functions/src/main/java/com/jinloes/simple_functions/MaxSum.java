package com.jinloes.simple_functions;

/**
 * Finds the max sum in an array.
 */
public class MaxSum {
    public static int findMax(int[] arr) {
        int maxSum = 0;
        int runningSum = 0;
        for (int i = 0; i < arr.length; i++) {
            int currentVal = arr[i];
            int currentSum = currentVal + runningSum;
            if (currentSum > maxSum) {
                maxSum = currentSum;
            }
            runningSum += currentVal;
            if (runningSum < 0) {
                runningSum = 0;
            }
        }
        return maxSum;
    }

    /**
     * Finds the max sum in a 2d array where you can only go right or down.
     *
     * @param arr array calculate the max sum
     * @return max sum
     */
    public static int findMax(int[][] arr) {
        Integer[][] maxArr = new Integer[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int previousTop = i != 0 ? maxArr[i - 1][j] : 0;
                int previousLeft = j != 0 ? maxArr[i][j - 1] : 0;
                maxArr[i][j] = Math.max(previousTop + arr[i][j], previousLeft + arr[i][j]);
            }
        }
        return maxArr[maxArr.length - 1][maxArr[0].length - 1];
    }
}