package com.jinloes.practice;

public class Solution {
    public static boolean canSplitEvenly(int[] values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        if (total % 2 != 0) {
            return false;
        }
        int target = total / 2;
        boolean[] reachable = new boolean[target + 1];
        reachable[0] = true;
        for (int value : values) {
            for (int sum = target; sum >= value; sum--) {
                if (reachable[sum - value]) {
                    reachable[sum] = true;
                }
            }
        }
        return reachable[target];
    }
}
