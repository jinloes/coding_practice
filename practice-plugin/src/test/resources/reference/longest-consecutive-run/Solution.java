package com.jinloes.practice;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static int longestRun(int[] numbers) {
        Set<Integer> values = new HashSet<>();
        for (int value : numbers) {
            values.add(value);
        }
        int best = 0;
        for (int value : values) {
            if (contains(values, (long) value - 1)) {
                continue;
            }
            int length = 1;
            while (contains(values, (long) value + length)) {
                length++;
            }
            best = Math.max(best, length);
        }
        return best;
    }

    private static boolean contains(Set<Integer> values, long candidate) {
        return candidate >= Integer.MIN_VALUE && candidate <= Integer.MAX_VALUE && values.contains((int) candidate);
    }
}
