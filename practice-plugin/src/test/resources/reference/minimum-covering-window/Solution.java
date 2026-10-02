package com.jinloes.practice;

public class Solution {
    public static String minimumWindow(String source, String required) {
        if (required.isEmpty() || required.length() > source.length()) {
            return "";
        }
        int[] needed = new int[128];
        for (int i = 0; i < required.length(); i++) {
            needed[required.charAt(i)]++;
        }
        int missing = required.length();
        int start = 0;
        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;
        for (int end = 0; end < source.length(); end++) {
            if (needed[source.charAt(end)]-- > 0) {
                missing--;
            }
            while (missing == 0) {
                if (end - start + 1 < bestLength) {
                    bestLength = end - start + 1;
                    bestStart = start;
                }
                if (++needed[source.charAt(start)] > 0) {
                    missing++;
                }
                start++;
            }
        }
        return bestLength == Integer.MAX_VALUE ? "" : source.substring(bestStart, bestStart + bestLength);
    }
}
