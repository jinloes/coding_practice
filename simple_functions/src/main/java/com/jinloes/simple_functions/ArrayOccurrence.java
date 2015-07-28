package com.jinloes.simple_functions;

/**
 * Finds the number of occurrences of a given number in an array in less then n time.
 */
public class ArrayOccurrence {
    public static int findOccurrences(int[] arr, int numToSearch) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int start = findFirstIndex(arr, numToSearch);
        if(start == -1) {
            return 0;
        }
        int end = findEndIndex(arr, numToSearch);
        return end - start + 1;
    }

    private static int findFirstIndex(int[] arr, int numToSearch) {
        int start = 0;
        int end = arr.length - 1;
        while (start <= end) {
            int mid = start + ((end - start) / 2);
            if (arr[mid] < numToSearch) {
                start = mid + 1;
            } else if ((mid == 0 || arr[mid - 1] < arr[mid]) && arr[mid] == numToSearch) {
                return mid;
            } else {
                end = mid - 1;
            }
        }
        return -1;
    }

    private static int findEndIndex(int[] arr, int numToSearch) {
        int start = 0;
        int end = arr.length - 1;
        while (start <= end) {
            int mid = start + ((end - start) / 2);
            if (arr[mid] > numToSearch) {
                end = mid - 1;
            } else if ((mid == arr.length - 1 || arr[mid + 1] > arr[mid]) && arr[mid] == numToSearch) {
                return mid;
            } else {
                start = mid + 1;
            }
        }
        return -1;
    }
}
