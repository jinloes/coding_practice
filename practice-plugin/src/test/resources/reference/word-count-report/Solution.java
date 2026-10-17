package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static List<String> countWords(String text) {
        Map<String, Integer> counts = new HashMap<>();
        for (String raw : text.split("\s+")) {
            String word = normalize(raw);
            if (!word.isEmpty()) counts.merge(word, 1, Integer::sum);
        }
        List<String> words = new ArrayList<>(counts.keySet());
        words.sort(java.util.Comparator.comparingInt(String::length).thenComparing(String::compareTo));
        List<String> report = new ArrayList<>(words.size());
        for (String word : words) report.add(counts.get(word) + " " + word);
        return report;
    }

    private static String normalize(String token) {
        int start = 0, end = token.length();
        while (start < end && !Character.isLetterOrDigit(token.charAt(start))) start++;
        while (end > start && !Character.isLetterOrDigit(token.charAt(end - 1))) end--;
        return token.substring(start, end).toLowerCase(java.util.Locale.ROOT);
    }
}
