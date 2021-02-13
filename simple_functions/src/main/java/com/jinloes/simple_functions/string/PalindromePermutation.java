package com.jinloes.simple_functions.string;

import java.util.HashMap;
import java.util.Map;

/**
 * Given a string return if it is a permutation of a palindrome.
 */
public class PalindromePermutation {

    public boolean isPalindromePermutation(String str) {
        if (str == null) {
            return true;
        }

        Map<Character, Integer> charCount = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            charCount.compute(Character.toLowerCase(str.charAt(i)),
                    (character, integer) -> integer == null ? 1 : integer + 1);
        }

        int numOdd = 0;

        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            if (entry.getValue() % 2 != 0) {
                numOdd++;
            }
        }
        if (str.length() % 2 == 0) {
            return numOdd == 2 || numOdd == 0;
        } else {
            return numOdd == 1 || numOdd == 0;
        }
    }
}
