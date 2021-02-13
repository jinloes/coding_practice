package com.jinloes.simple_functions.string;

import java.util.HashSet;
import java.util.Set;

/**
 * Given two strings, write a method to decide if one is a permutation of the other.
 */
public class Permutation {

    public boolean isPermutation(String s1, String s2) {
        if (s1 == null && s2 != null) {
            return false;
        }
        if (s2 == null && s1 != null) {
            return false;
        }
        if (s1 == null) {
            return true;
        }

        Set<Character> s1Chars = new HashSet<>();
        Set<Character> s2Chars = new HashSet<>();

        for (int i = 0; i < s1.length(); i++) {
            s1Chars.add(s1.charAt(i));
        }

        for (int i = 0; i < s2.length(); i++) {
            s2Chars.add(s2.charAt(i));
        }

        return s1Chars.equals(s2Chars);
    }
}
