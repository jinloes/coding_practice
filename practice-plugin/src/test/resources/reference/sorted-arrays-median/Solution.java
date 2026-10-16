package com.jinloes.practice;

public class Solution {
    public static double median(int[] first, int[] second) {
        if (first.length > second.length) {
            return median(second, first);
        }
        int m = first.length;
        int n = second.length;
        if (n == 0) {
            throw new IllegalArgumentException("Both arrays are empty");
        }
        int half = (m + n + 1) / 2;
        int low = 0;
        int high = m;
        while (low <= high) {
            int i = (low + high) >>> 1;
            int j = half - i;
            long leftFirst = i == 0 ? Long.MIN_VALUE : first[i - 1];
            long rightFirst = i == m ? Long.MAX_VALUE : first[i];
            long leftSecond = j == 0 ? Long.MIN_VALUE : second[j - 1];
            long rightSecond = j == n ? Long.MAX_VALUE : second[j];
            if (leftFirst <= rightSecond && leftSecond <= rightFirst) {
                long leftMax = Math.max(leftFirst, leftSecond);
                if ((m + n) % 2 == 1) {
                    return leftMax;
                }
                long rightMin = Math.min(rightFirst, rightSecond);
                return (leftMax + rightMin) / 2.0;
            }
            if (leftFirst > rightSecond) {
                high = i - 1;
            } else {
                low = i + 1;
            }
        }
        throw new IllegalStateException("Arrays are not sorted");
    }
}
