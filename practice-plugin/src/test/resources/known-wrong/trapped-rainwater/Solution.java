package com.jinloes.practice;

public class Solution {
    public static long trap(int[] heights) {
        long water = 0;
        int leftMax = 0;
        for (int height : heights) {
            leftMax = Math.max(leftMax, height);
            water += leftMax - height;
        }
        return water;
    }
}
