package com.jinloes.practice;

public class Solution {
    public static int minimumSpeed(int[] piles, int hours) {
        int low = 1;
        int high = 1;
        for (int pile : piles) {
            high = Math.max(high, pile);
        }
        while (low < high) {
            int mid = low + (high - low) / 2;
            long needed = 0;
            for (int pile : piles) {
                needed += pile / mid;
            }
            if (needed <= hours) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }
}
