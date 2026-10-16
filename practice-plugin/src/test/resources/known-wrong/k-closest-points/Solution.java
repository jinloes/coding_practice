package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int[][] closest(int[][] points, int k) {
        if (k < 0 || k > points.length) {
            throw new IllegalArgumentException("k must be between 0 and " + points.length);
        }
        int[][] sorted = points.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(Math.abs(a[0]) + Math.abs(a[1]), Math.abs(b[0]) + Math.abs(b[1])));
        return Arrays.copyOf(sorted, k);
    }
}
