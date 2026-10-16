package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<String>> partitions(String text) {
        List<List<String>> result = new ArrayList<>();
        split(text, 0, new ArrayList<>(), result);
        return result;
    }

    private static void split(String text, int start, List<String> pieces, List<List<String>> result) {
        if (start == text.length()) {
            result.add(new ArrayList<>(pieces));
            return;
        }
        for (int end = start; end < text.length(); end++) {
            if (text.charAt(start) == text.charAt(end)) {
                pieces.add(text.substring(start, end + 1));
                split(text, end + 1, pieces, result);
                pieces.remove(pieces.size() - 1);
            }
        }
    }
}
