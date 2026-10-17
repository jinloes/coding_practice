package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static boolean hasOneCharacterDifference(String[] words) {
        Map<String, String> seen = new HashMap<>();
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                String signature = word.substring(0, i) + '*' + word.substring(i + 1);
                String previous = seen.putIfAbsent(signature, word);
                if (previous != null && !previous.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }
}
