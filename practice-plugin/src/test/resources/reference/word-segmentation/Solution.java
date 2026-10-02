package com.jinloes.practice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    public static boolean canSegment(String text, List<String> dictionary) {
        Set<String> words = new HashSet<>(dictionary);
        int longest = 0;
        for (String word : words) {
            longest = Math.max(longest, word.length());
        }
        boolean[] reachable = new boolean[text.length() + 1];
        reachable[0] = true;
        for (int end = 1; end <= text.length(); end++) {
            for (int start = end - 1; start >= 0 && end - start <= longest; start--) {
                if (reachable[start] && words.contains(text.substring(start, end))) {
                    reachable[end] = true;
                    break;
                }
            }
        }
        return reachable[text.length()];
    }
}
