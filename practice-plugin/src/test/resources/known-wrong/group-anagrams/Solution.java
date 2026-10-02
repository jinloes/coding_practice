package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static List<List<String>> groupAnagrams(List<String> words) {
        Map<Integer, List<String>> groups = new HashMap<>();
        for (String word : words) {
            int sum = word.length() * 1000;
            for (int i = 0; i < word.length(); i++) {
                sum += word.charAt(i);
            }
            groups.computeIfAbsent(sum, key -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }
}
