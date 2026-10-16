package com.jinloes.practice;

public class Solution {
    public static int countAssignments(int[] values, int target) {
        long total = 0;
        for (int value : values) {
            total += value;
        }
        long doubled = total + target;
        if (doubled < 0 || doubled % 2 != 0 || Math.abs((long) target) > total) {
            return 0;
        }
        int wanted = (int) (doubled / 2);
        int[] ways = new int[wanted + 1];
        ways[0] = 1;
        for (int value : values) {
            if (value == 0) {
                continue;
            }
            for (int sum = wanted; sum >= value; sum--) {
                ways[sum] += ways[sum - value];
            }
        }
        return ways[wanted];
    }
}
