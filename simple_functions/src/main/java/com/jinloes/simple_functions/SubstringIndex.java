package com.jinloes.simple_functions;

/**
 * Finds the starting index of a substring in another string.
 */
public class SubstringIndex {
    public static int findSubstringIndex(String subString, String string) {
        if (subString == null || string == null) {
            return -1;
        }
        int matchCount = 0;
        for (int i = 0; i < string.length(); i++) {
            if (matchCount == subString.length()) {
                return i - matchCount;
            } else if (string.charAt(i) == subString.charAt(matchCount)) {
                matchCount++;
            } else {
                matchCount = 0;
            }
        }
        if (matchCount == subString.length()) {
            return string.length() - matchCount;
        }
        return -1;
    }
}
