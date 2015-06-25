package com.jinloes.simple_functions;

/**
 * Calculates the max donation from neighbors with wrapping.
 */
public class MaxDonation {
    public static int getMax(int[] arr) {
        if (arr.length == 1) {
            return arr[0];
        }
        if (arr.length == 2) {
            return Math.max(arr[0], arr[1]);
        }
        int max = 0;
        int[] solArr = new int[arr.length];
        solArr[0] = arr[0];
        solArr[1] = arr[1];
        for (int i = 2; i < arr.length; i++) {
            for (int j = 0; j < i - 1; j++) {
                if (solArr[j] + arr[i] > solArr[i]) {
                    solArr[i] = solArr[j] + arr[i];
                }
            }
            if (i == arr.length - 1) {
                if (arr[i] > arr[0]) {
                    solArr[i] = solArr[i] - arr[0];
                } else {
                    solArr[i] = solArr[i] - arr[i];
                }
            }
        }
        for (int value : solArr) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int getMaxAlternative(int[] arr) {
        if (arr.length == 1) {
            return arr[0];
        }
        if (arr.length == 2) {
            return Math.max(arr[0], arr[1]);
        }
        int previousPrevious = arr[0];
        int previous = Math.max(arr[0], arr[1]);
        for (int i = 2; i < arr.length; i++) {
            int tmp = Math.max(previousPrevious + arr[i], previous);
            previousPrevious = previous;
            previous = tmp;
        }
        if (arr[arr.length - 1] > arr[0]) {
            previous -= arr[0];
        } else {
            previous -= arr[arr.length - 1];
        }
        return Math.max(previous, previousPrevious);
    }
}
