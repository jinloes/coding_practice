package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {
    public static int ladderLength(String begin, String end, List<String> words) {
        Set<String> allowed = new HashSet<>(words);
        if (!allowed.contains(end)) {
            return 0;
        }
        Map<String, List<String>> buckets = new HashMap<>();
        allowed.add(begin);
        for (String word : allowed) {
            char[] letters = word.toCharArray();
            for (int i = 0; i < letters.length; i++) {
                char saved = letters[i];
                letters[i] = '*';
                buckets.computeIfAbsent(new String(letters), key -> new java.util.ArrayList<>()).add(word);
                letters[i] = saved;
            }
        }
        Set<String> seen = new HashSet<>();
        ArrayDeque<String> level = new ArrayDeque<>();
        level.add(begin);
        seen.add(begin);
        int length = 1;
        while (!level.isEmpty()) {
            for (int remaining = level.size(); remaining > 0; remaining--) {
                String word = level.remove();
                if (word.equals(end)) {
                    return length;
                }
                char[] letters = word.toCharArray();
                for (int i = 0; i < letters.length; i++) {
                    char saved = letters[i];
                    letters[i] = '*';
                    List<String> next = buckets.get(new String(letters));
                    letters[i] = saved;
                    for (String candidate : next) {
                        if (seen.add(candidate)) {
                            level.add(candidate);
                        }
                    }
                }
            }
            length++;
        }
        return 0;
    }
}
