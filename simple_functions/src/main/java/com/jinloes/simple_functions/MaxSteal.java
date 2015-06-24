package com.jinloes.simple_functions;

/**
 * Steals the max amount from houses. A thief cannot steal from a house next to one he has already stolen from.
 */
public class MaxSteal {
    public static int steal(int[] arr) {
        if (arr.length == 0) {
            return 0;
        }
        if (arr.length == 1) {
            return arr[0];
        }
        if (arr.length == 2) {
            return Math.max(arr[0], arr[1]);
        }
        int maxSteal = 0;
        int previousPreviousValue = arr[0];
        int previousValue = Math.max(arr[0], arr[1]);
        for(int i = 2; i < arr.length; i++) {
            maxSteal = Math.max(previousPreviousValue + arr[i], previousValue);
            previousPreviousValue = previousValue;
            previousValue = maxSteal;
        }
        return maxSteal;
    }
}
