package com.jinloes.practice;

public class Solution {
    public static String minimumWindow(String source, String required) {
        if (required.isEmpty()) {
            return "";
        }
        boolean[] wanted = new boolean[128];
        int distinct = 0;
        for (int i = 0; i < required.length(); i++) {
            if (!wanted[required.charAt(i)]) {
                wanted[required.charAt(i)] = true;
                distinct++;
            }
        }
        int[] seen = new int[128];
        int covered = 0;
        int start = 0;
        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;
        for (int end = 0; end < source.length(); end++) {
            char c = source.charAt(end);
            if (wanted[c] && ++seen[c] == 1) {
                covered++;
            }
            while (covered == distinct) {
                if (end - start + 1 < bestLength) {
                    bestLength = end - start + 1;
                    bestStart = start;
                }
                char left = source.charAt(start++);
                if (wanted[left] && --seen[left] == 0) {
                    covered--;
                }
            }
        }
        return bestLength == Integer.MAX_VALUE ? "" : source.substring(bestStart, bestStart + bestLength);
    }
}
