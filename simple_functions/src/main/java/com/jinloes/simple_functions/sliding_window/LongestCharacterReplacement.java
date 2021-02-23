package com.jinloes.simple_functions.sliding_window;

import java.util.HashMap;
import java.util.Map;

/**
 * Given a string s that consists of only uppercase English letters, you can perform at most k operations on that string.
 * <p>
 * In one operation, you can choose any character of the string and change it to any other uppercase English character.
 * <p>
 * Find the length of the longest sub-string containing all repeating letters you can get after performing the above operations.
 */
public class LongestCharacterReplacement {

    public int characterReplacement(String s, int k) {
        int maxSub = 0;
        int start = 0;
        int end = 0;
        int maxCount = 0;
        Map<Character, Integer> countMap = new HashMap<>();

        while (end < s.length()) {
            char current = s.charAt(end);
            char startChar = s.charAt(start);
            Integer newVal = countMap.compute(current, ((character, integer) -> integer == null ? 1 : integer + 1));
            maxCount = Math.max(maxCount, newVal);
            if (end - start + 1 > k + maxCount) {
                countMap.compute(startChar, (character, integer) -> integer == null ? 0 : integer - 1);
                start++;
            }
            maxSub = Math.max(maxSub, end - start + 1);
            end++;
        }
        return maxSub;
    }
}
