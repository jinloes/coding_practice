package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Given a word, find all anagrams of the word in a given input string.
 * <p>
 * # Example
 * <p>
 * Input: CABCDBACDABD
 * Word: AB
 * > Output: (1,5,9)
 */
public class FindAnagrams {
    public List<Integer> find(String input, String word) {
        List<Integer> result = new ArrayList<>();

        if (input == null || word == null || word.length() > input.length()) {
            return result;
        }

        char[] wordArray = word.toCharArray();
        Arrays.sort(wordArray);
        int windowSize = word.length();

        for (int i = 0; i <= input.length() - windowSize; i++) {
            char[] substr = input.substring(i, i + windowSize).toCharArray();
            Arrays.sort(substr);
            if (Arrays.equals(wordArray, substr)) {
                result.add(i);
            }
        }

        return result;
    }
}
