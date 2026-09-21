package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void treatsTwoEmptyWordsAsAnagrams() {
        assertAnagram("", "");
    }

    @Test
    void rejectsAnEmptyWordAgainstALetter() {
        assertNotAnagram("", "a");
        assertNotAnagram("a", "");
    }

    @Test
    void acceptsAWordComparedWithItself() {
        assertAnagram("microsoft", "microsoft");
        assertAnagram("z", "z");
    }

    @Test
    void rejectsWordsOfDifferentLengths() {
        assertNotAnagram("ab", "aab");
        assertNotAnagram("aab", "ab");
    }

    @Test
    void rejectsAWordThatSharesAPrefixOnly() {
        assertNotAnagram("abcd", "abce");
    }

    @Test
    void handlesRepeatedLettersInBothWords() {
        assertAnagram("aabbcc", "cbacba");
        assertNotAnagram("aabbcc", "aabbbc");
    }

    @Test
    void handlesEveryLetterOfTheAlphabet() {
        assertAnagram("abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba");
        assertNotAnagram("abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcbb");
    }

    @Test
    void isSymmetricOnLongWords() {
        String first = "a".repeat(500) + "b".repeat(500);
        String second = "b".repeat(500) + "a".repeat(500);
        assertAnagram(first, second);
        assertAnagram(second, first);
    }

    private static void assertAnagram(String first, String second) {
        assertThat(isAnagram(first, second))
                .as("%s must accept two words built from the same letters", call(first, second))
                .isTrue();
    }

    private static void assertNotAnagram(String first, String second) {
        assertThat(isAnagram(first, second))
                .as("%s must reject words that do not use the same letters", call(first, second))
                .isFalse();
    }

    private static boolean isAnagram(String first, String second) {
        try {
            return Solution.isAnagram(first, second);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(first, second), thrown), thrown);
        }
    }

    private static String call(String first, String second) {
        return "isAnagram(\"%s\", \"%s\")".formatted(abbreviate(first), abbreviate(second));
    }

    private static String abbreviate(String word) {
        return word.length() <= 40 ? word : word.substring(0, 40) + "... (" + word.length() + " letters)";
    }
}
