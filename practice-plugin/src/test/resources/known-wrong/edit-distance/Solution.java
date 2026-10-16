package com.jinloes.practice;

public class Solution {
    public static int minEdits(String from, String to) {
        int[] previous = new int[to.length() + 1];
        int[] current = new int[to.length() + 1];
        for (int j = 0; j <= to.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= from.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= to.length(); j++) {
                if (from.charAt(i - 1) == to.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = Math.min(previous[j], current[j - 1]) + 1;
                }
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[to.length()];
    }
}
