package com.jinloes.practice;

public class Solution {
    public static int search(int[] rotated, int target) {
        int low = 0;
        int high = rotated.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (rotated[mid] == target) {
                return mid;
            }
            if (rotated[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
