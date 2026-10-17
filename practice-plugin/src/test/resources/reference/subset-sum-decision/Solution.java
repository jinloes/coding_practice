package com.jinloes.practice;

public class Solution {
    public static boolean hasSubsetSum(int[] numbers, int target) {
        if (target < 0) return false;
        boolean[] reachable = new boolean[target + 1]; reachable[0] = true;
        for (int number : numbers) {
            if (number > target) continue;
            for (int sum = target; sum >= number; sum--) reachable[sum] |= reachable[sum - number];
        }
        return reachable[target];
    }
}
