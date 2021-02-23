package com.jinloes.simple_functions.sliding_window;

import java.util.HashMap;
import java.util.Map;

/**
 * Given a string you need to return longest possible substring that has exactly K unique characters.
 * If there are more than one substring of longest possible length, then return any one of them.
 */
public class LongestSubstringKUniqueChars {
    private String maxSubString = "";
    private Map<Character, Integer> charCount = new HashMap<>();

    public String kUniques(String s, int k) {
        int start = 0;
        int end = 0;

        while (end < s.length()) {
            char nextChar = s.charAt(end);
            if (charCount.keySet().size() == k) {
                if (end - start > maxSubString.length()) {
                    maxSubString = s.substring(start, end);
                }
            }
            if (charCount.size() == k
                    && charCount.getOrDefault(nextChar, 0) == 0) {
                charCount.compute(s.charAt(start), (character, integer) -> {
                    if (integer == null) {
                        return null;
                    }
                    return integer - 1 > 0 ? integer - 1 : null;
                });
                start++;
            } else {
                charCount.compute(nextChar, (character, integer) -> integer != null ? integer + 1 : 1);
                end++;
            }
        }

        return maxSubString;
    }
}
