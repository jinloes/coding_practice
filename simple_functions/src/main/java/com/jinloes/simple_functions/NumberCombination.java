package com.jinloes.simple_functions;

/**
 * Detects if a combination of the numbers in an array sum up to some value.
 */
public class NumberCombination {
    public static boolean findCombo(int[] arr, int sum) {
        int i = 0;
        while(i < arr.length -1 && arr[i] > sum ) {
            i++;
        }
        return findRecursively(arr, i, sum, 0);
    }
    private static boolean findRecursively(int[] arr, int start, int sum, int runningSum) {
        if(runningSum == sum) {
            return true;
        }
        if(start >= arr.length) {
            return false;
        }
        for(int i = start; i < arr.length; i++) {
            if(findRecursively(arr, i + 1, sum, runningSum + arr[i])) {
                return true;
            }
        }
        return false;
    }
}
