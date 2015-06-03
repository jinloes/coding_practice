package com.jinloes.simple_functions;

/**
 * Finds the starting index of a substring in another string.
 */
public class SubstringIndex {
    public static int findSubstringIndex(String subString, String string) {
        if(subString == null || string == null) {
            return -1;
        }
        int j = 0;
        for(int i = 0; i < string.length(); i++) {
            if(j == subString.length()) {
                return i - j;
            }
            else if(string.charAt(i) == subString.charAt(j)) {
                j++;
            } else {
                j = 0;
            }
        }
        if(j == subString.length()) {
            return string.length() - j;
        }
        return -1;
    }
}
