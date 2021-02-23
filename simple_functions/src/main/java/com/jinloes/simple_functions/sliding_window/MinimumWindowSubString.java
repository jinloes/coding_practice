package com.jinloes.simple_functions.sliding_window;

import java.util.HashMap;
import java.util.Map;

/**
 * Given two strings s and t, return the minimum window in s which will contain all the characters in t.
 * If there is no such window in s that covers all characters in t, return the empty string "".
 * <p>
 * Note that If there is such a window, it is guaranteed that there will always be only one unique minimum window in s.
 */
public class MinimumWindowSubString {
    public String minWindow(String s, String t) {
        if (t.length() > s.length()) {
            return "";
        }

        Map<Character, Integer> charCount = new HashMap<>();
        Map<Character, Integer> tCharCount = new HashMap<>();

        for (int i = 0; i < t.length(); i++) {
            tCharCount.compute(t.charAt(i), (character, integer) -> integer == null ? 1 : integer + 1);
        }

        int matches = 0;
        int start = 0;
        int minLength = Integer.MAX_VALUE;
        int minStart = -1;

        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);

            int newVal = charCount.compute(current, (character, integer) -> integer == null ? 1 : integer + 1);

            if (newVal <= tCharCount.getOrDefault(current, 0)) {
                matches++;
            }

            if (matches == t.length()) {
                // Try to minimize the window
                while (charCount.get(s.charAt(start)) > tCharCount.getOrDefault(s.charAt(start), 0)
                        || charCount.get(s.charAt(start)) == 0) {

                    if (charCount.get(s.charAt(start)) > tCharCount.getOrDefault(s.charAt(start), 0)) {
                        charCount.compute(s.charAt(start), ((character, integer) -> integer - 1));
                    }
                    start++;
                }

                // update window size
                int lenWindow = i - start + 1;
                if (minLength > lenWindow) {
                    minLength = lenWindow;
                    minStart = start;
                }
            }
        }
        if (minStart == -1) {
            return "";
        }
        return s.substring(minStart, minStart + minLength);
    }
}
