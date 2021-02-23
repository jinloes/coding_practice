package com.jinloes.simple_functions.sliding_window;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringKRepeatingChars {
    public int longestSubstring(String s, int k) {
        Map<Character, Integer> countMap = new HashMap<>();

        Set<Character> uniqueChars = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            uniqueChars.add(s.charAt(i));
        }

        int maxUnique = uniqueChars.size();

        int longestSubStr = 0;

        for (int i = 1; i <= maxUnique; i++) {
            int start = 0;
            int end = 0;
            int countAtK = 0;
            countMap.clear();

            while (end < s.length()) {
                char current = s.charAt(end);
                if (countMap.keySet().size() <= i) {
                    countMap.compute(current, (character, integer) -> integer != null ? integer + 1 : 1);
                    if (countMap.get(current) == k) {
                        countAtK++;
                    }
                    end++;
                } else {
                    if (countMap.getOrDefault(s.charAt(start), 0) == k) {
                        countAtK--;
                    }
                    countMap.compute(s.charAt(start), (character, integer) -> {
                        if (integer == null) {
                            return null;
                        }
                        return integer - 1 == 0 ? null : integer - 1;
                    });
                    start++;
                }

                if (i == countMap.keySet().size() && countMap.keySet().size() == countAtK) {
                    longestSubStr = Math.max(end - start, longestSubStr);
                }
            }
        }
        return longestSubStr;
    }
}
