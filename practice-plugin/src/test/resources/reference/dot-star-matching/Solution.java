package com.jinloes.practice;

public class Solution {
    public static boolean matches(String text, String pattern) {
        int m = pattern.length();
        boolean[] previous = new boolean[m + 1];
        boolean[] current = new boolean[m + 1];
        previous[0] = true;
        for (int j = 2; j <= m; j++) {
            previous[j] = pattern.charAt(j - 1) == '*' && previous[j - 2];
        }
        for (int i = 1; i <= text.length(); i++) {
            char letter = text.charAt(i - 1);
            current[0] = false;
            for (int j = 1; j <= m; j++) {
                char element = pattern.charAt(j - 1);
                if (element == '*') {
                    char starred = pattern.charAt(j - 2);
                    current[j] = current[j - 2]
                            || (previous[j] && (starred == '.' || starred == letter));
                } else {
                    current[j] = previous[j - 1] && (element == '.' || element == letter);
                }
            }
            boolean[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[m];
    }
}
