package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static List<List<String>> groupAnagrams(List<String> words) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : words) {
            groups.computeIfAbsent(signature(word), key -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }

    private static String signature(String word) {
        int[] counts = new int[26];
        for (int i = 0; i < word.length(); i++) {
            counts[word.charAt(i) - 'a']++;
        }
        StringBuilder key = new StringBuilder();
        for (int letter = 0; letter < 26; letter++) {
            if (counts[letter] > 0) {
                key.append((char) ('a' + letter)).append(counts[letter]);
            }
        }
        return key.toString();
    }
}
