package com.jinloes.practice;

public class Solution {
    public static int lcsLength(String first, String second) {
        int best = 0;
        int[] previous = new int[second.length() + 1];
        int[] current = new int[second.length() + 1];
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    current[j] = previous[j - 1] + 1;
                    best = Math.max(best, current[j]);
                } else {
                    current[j] = 0;
                }
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return best;
    }
}
