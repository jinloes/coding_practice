package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void returnsEmptyForNoWords() {
        assertPrefix("");
    }

    @Test
    void returnsEmptyWhenAnyWordIsEmpty() {
        assertPrefix("", "", "abc");
        assertPrefix("", "abc", "");
        assertPrefix("", "");
    }

    @Test
    void returnsTheWholeWordWhenEveryWordIsTheSame() {
        assertPrefix("same", "same", "same", "same");
    }

    @Test
    void returnsAShortWordThatPrefixesTheRest() {
        assertPrefix("ab", "abcd", "ab", "abc");
        assertPrefix("ab", "ab", "abcd");
    }

    @Test
    void detectsADisagreementInTheLastWordOnly() {
        assertPrefix("inter", "interview", "internet", "interval", "intern", "interstate");
        assertPrefix("", "apple", "apricot", "banana");
    }

    @Test
    void comparesEveryWordNotJustTheFirstTwo() {
        assertPrefix("pre", "prefix", "prefer", "pretty");
    }

    @Test
    void handlesSingleLetterWords() {
        assertPrefix("a", "a", "a");
        assertPrefix("", "a", "b");
    }

    @Test
    void handlesManyLongWords() {
        String shared = "abcdefghij".repeat(50);
        String[] words = new String[500];
        for (int i = 0; i < words.length; i++) {
            words[i] = shared + (char) ('a' + i % 26) + "tail";
        }
        assertPrefix(shared, words);
    }

    @Test
    void leavesTheArrayUnchanged() {
        String[] words = {"flower", "flow", "flight"};
        String[] before = words.clone();
        prefixOf(words);
        assertThat(words).as("longestCommonPrefix must not mutate its input").containsExactly(before);
    }

    private static void assertPrefix(String expected, String... words) {
        assertThat(prefixOf(words)).as("%s", call(words)).isEqualTo(expected);
    }

    private static String prefixOf(String[] words) {
        try {
            return Solution.longestCommonPrefix(words);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(words), thrown), thrown);
        }
    }

    private static String call(String[] words) {
        String shown = words.length <= 6 && Arrays.stream(words).allMatch(word -> word.length() <= 20)
                ? Arrays.toString(words)
                : "[" + words.length + " words starting with \""
                        + words[0].substring(0, Math.min(20, words[0].length())) + "...\"]";
        return "longestCommonPrefix(%s)".formatted(shown);
    }
}
