package com.jinloes.practice;

public class Solution {
    public static long largestRectangle(int[] heights) {
        int n = heights.length;
        int[] stack = new int[n + 1];
        int top = 0;
        long best = 0;
        for (int i = 0; i < n; i++) {
            while (top > 0 && heights[stack[top - 1]] >= heights[i]) {
                int height = heights[stack[--top]];
                int left = top == 0 ? 0 : stack[top - 1] + 1;
                best = Math.max(best, (long) height * (i - left));
            }
            stack[top++] = i;
        }
        return best;
    }
}
