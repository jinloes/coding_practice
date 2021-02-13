package com.jinloes.simple_functions.string;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Determine if a string has all unique characters.
 * What if you cannot use additional data structures?
 */
public class IsUnique {

    public boolean isUnique(String str) {
        Set<Character> foundChars = new HashSet<>();

        for (int i = 0; i < str.length(); i++) {
            if (foundChars.contains(str.charAt(i))) {
                return false;
            }
            foundChars.add(str.charAt(i));
        }

        return true;
    }

    public boolean isUniqueNoAdditional(String str) {
        if (str.length() < 1) {
            return true;
        }
        char[] charArr = str.toCharArray();

        Arrays.sort(charArr);


        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                return false;
            }
        }

        return true;

    }
}
