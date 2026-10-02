package com.jinloes.practice;

public class Solution {
    public static long maxArea(int[] heights) {
        long best = 0;
        int left = 0;
        int right = heights.length - 1;
        while (left < right) {
            long area = (long) Math.min(heights[left], heights[right]) * (right - left);
            best = Math.max(best, area);
            if (heights[left] > heights[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }
}
