package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<String>> partitions(String text) {
        int n = text.length();
        boolean[][] palindrome = new boolean[n][n];
        for (int start = n - 1; start >= 0; start--) {
            for (int end = start; end < n; end++) {
                palindrome[start][end] = text.charAt(start) == text.charAt(end)
                        && (end - start < 2 || palindrome[start + 1][end - 1]);
            }
        }
        List<List<String>> result = new ArrayList<>();
        split(text, palindrome, 0, new ArrayList<>(), result);
        return result;
    }

    private static void split(String text, boolean[][] palindrome, int start, List<String> pieces,
                              List<List<String>> result) {
        if (start == text.length()) {
            result.add(new ArrayList<>(pieces));
            return;
        }
        for (int end = start; end < text.length(); end++) {
            if (palindrome[start][end]) {
                pieces.add(text.substring(start, end + 1));
                split(text, palindrome, end + 1, pieces, result);
                pieces.remove(pieces.size() - 1);
            }
        }
    }
}
