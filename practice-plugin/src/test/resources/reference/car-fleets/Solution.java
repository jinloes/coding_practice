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
        long aheadDistance = 0;
        long aheadSpeed = 1;
        for (int i = n - 1; i >= 0; i--) {
            long distance = target - (cars[i] >> 32);
            long speed = cars[i] & 0xFFFFFFFFL;
            // distance / speed > aheadDistance / aheadSpeed, using cross-multiplication
            if (fleets == 0 || distance * aheadSpeed > aheadDistance * speed) {
                fleets++;
                aheadDistance = distance;
                aheadSpeed = speed;
            }
        }
        return fleets;
    }
}
