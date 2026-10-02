package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int longestRun(int[] numbers) {
        if (numbers.length == 0) {
            return 0;
        }
        int[] sorted = numbers.clone();
        Arrays.sort(sorted);
        int best = 1;
        int current = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] - sorted[i - 1] == 1) {
                current++;
            } else {
                current = 1;
            }
            best = Math.max(best, current);
        }
        return best;
    }
}
