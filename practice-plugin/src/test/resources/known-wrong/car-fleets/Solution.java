package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int countFleets(int target, int[] positions, int[] speeds) {
        int n = positions.length;
        long[] cars = new long[n];
        for (int i = 0; i < n; i++) {
            cars[i] = ((long) positions[i] << 32) | speeds[i];
        }
        Arrays.sort(cars);
        int fleets = 0;
        double aheadTime = 0;
        for (int i = n - 1; i >= 0; i--) {
            double time = (double) (target - (cars[i] >> 32)) / (cars[i] & 0xFFFFFFFFL);
            if (fleets == 0 || time >= aheadTime) {
                fleets++;
                aheadTime = time;
            }
        }
        return fleets;
    }
}
