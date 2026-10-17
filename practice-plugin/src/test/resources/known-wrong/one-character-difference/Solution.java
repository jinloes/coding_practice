package com.jinloes.practice;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static boolean hasOneCharacterDifference(String[] words) {
        Set<String> seen = new HashSet<>();
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                String signature = word.substring(0, i) + '*' + word.substring(i + 1);
                if (!seen.add(signature)) {
                    return true;
                }
            }
        }
        return false;
    }
}
