package com.jinloes.practice;

public class Solution {
    public static int findMinimum(int[] rotated) {
        int low = 0;
        int high = rotated.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (rotated[mid] > rotated[high]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return rotated[low];
    }
}
