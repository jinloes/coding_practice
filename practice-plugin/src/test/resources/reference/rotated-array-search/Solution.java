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
            if (rotated[low] <= rotated[mid]) {
                if (rotated[low] <= target && target < rotated[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (rotated[mid] < target && target <= rotated[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }
}
