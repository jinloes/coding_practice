package com.jinloes.simple_functions.sliding_window;

import java.util.HashMap;
import java.util.Map;

/**
 * Given two strings s and t, return the minimum window in s which will contain all the characters in t.
 * If there is no such window in s that covers all characters in t, return the empty string "".
 * <p>
 * Note that If there is such a window, it is guaranteed that there will always be only one unique minimum window in s.
 */
class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        Map<Character, Integer> tCountMap = new HashMap<>();
        Map<Character, Integer> sCountMap = new HashMap<>();

        for (char c : t.toCharArray()) {
            int count = tCountMap.getOrDefault(c, 0);
            tCountMap.put(c, count + 1);
        }

        int left = 0;
        int right = 0;
        int minSize = Integer.MAX_VALUE;
        int minLeft = 0;
        int formed = 0;

        while (right < s.length()) {
            char c = s.charAt(right);
            if (!tCountMap.containsKey(c)) {
                right++;
                continue;
            }
            int count = sCountMap.getOrDefault(c, 0);
            sCountMap.put(c, count + 1);

            if (count + 1 == tCountMap.get(c)) {
                formed++;
            }

            while (left <= right && formed == tCountMap.size()) {
                if (right - left + 1 < minSize) {
                    minSize = right - left + 1;
                    minLeft = left;
                }
                char leftC = s.charAt(left);
                if (!sCountMap.containsKey(leftC)) {
                    left++;
                    continue;
                }
                int leftCount = sCountMap.get(leftC);
                if (leftCount - 1 < tCountMap.get(leftC)) {
                    formed--;
                }
                sCountMap.put(leftC, leftCount - 1);
                left++;
            }
            right++;

        }

        return minSize != Integer.MAX_VALUE ? s.substring(minLeft, minLeft + minSize) : "";
    }
}
