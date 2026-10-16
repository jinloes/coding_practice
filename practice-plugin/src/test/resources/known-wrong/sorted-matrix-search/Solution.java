package com.jinloes.practice;

public class Solution {
    public static boolean containsValue(int[][] grid, int target) {
        if (grid.length == 0 || grid[0].length == 0) {
            return false;
        }
        int columns = grid[0].length;
        int low = 0;
        int high = grid.length * columns - 1;
        while (low < high) {
            int mid = (low + high) >>> 1;
            int value = grid[mid / columns][mid % columns];
            if (value == target) {
                return true;
            }
            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }
}
