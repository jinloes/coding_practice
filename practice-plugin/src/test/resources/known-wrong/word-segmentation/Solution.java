package com.jinloes.practice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    public static boolean canSegment(String text, List<String> dictionary) {
        Set<String> words = new HashSet<>(dictionary);
        int position = 0;
        while (position < text.length()) {
            int matched = -1;
            for (int end = text.length(); end > position; end--) {
                if (words.contains(text.substring(position, end))) {
                    matched = end;
                    break;
                }
            }
            if (matched < 0) {
                return false;
            }
            position = matched;
        }
        return true;
    }
}
