package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void groupsTypicalWords() {
        assertGroups(List.of("stop", "pots", "cat", "tops", "act"),
                List.of(List.of("stop", "pots", "tops"), List.of("cat", "act")));
    }

    @Test
    void groupsEmptyWordsTogether() {
        assertGroups(List.of("", ""), List.of(List.of("", "")));
    }

    @Test
    void keepsSingleWord() {
        assertGroups(List.of("abc"), List.of(List.of("abc")));
    }

    @Test
    void returnsNoGroupsForEmptyInput() {
        assertGroups(List.of(), List.of());
    }

    @Test
    void keepsDuplicatesAsSeparateEntries() {
        assertGroups(List.of("ab", "ba", "ab"), List.of(List.of("ab", "ba", "ab")));
    }

    @Test
    void separatesWordsWithEqualLetterSums() {
        assertGroups(List.of("ad", "bc", "da", "cb"),
                List.of(List.of("ad", "da"), List.of("bc", "cb")));
    }

    @Test
    void separatesWordsWithDifferentLetterCounts() {
        assertGroups(List.of("aab", "abb", "bba", "baa"),
                List.of(List.of("aab", "baa"), List.of("abb", "bba")));
    }

    @Test
    void separatesEmptyWordFromLetters() {
        assertGroups(List.of("", "a", ""), List.of(List.of("", ""), List.of("a")));
    }

    @Test
    void separatesWordsOfDifferentLengths() {
        assertGroups(List.of("aa", "a", "aaa"),
                List.of(List.of("aa"), List.of("a"), List.of("aaa")));
    }

    @Test
    void handlesLongRepeatedLetterWords() {
        String first = "z".repeat(500) + "a";
        String second = "a" + "z".repeat(500);
        assertGroups(List.of(first, second, "z".repeat(501)),
                List.of(List.of(first, second), List.of("z".repeat(501))));
    }

    @Test
    void doesNotMutateInput() {
        List<String> words = new ArrayList<>(List.of("tab", "bat", "x"));
        Solution.groupAnagrams(words);
        assertThat(words).as("input list must be left unchanged").containsExactly("tab", "bat", "x");
    }

    private static void assertGroups(List<String> words, List<List<String>> expected) {
        List<List<String>> actual = Solution.groupAnagrams(new ArrayList<>(words));
        assertThat(normalize(actual))
                .as("groupAnagrams(%s) must group the words into %s", words, expected)
                .isEqualTo(normalize(expected));
    }

    private static List<String> normalize(List<List<String>> groups) {
        List<String> result = new ArrayList<>();
        for (List<String> group : groups) {
            List<String> sorted = new ArrayList<>(group);
            Collections.sort(sorted);
            result.add(sorted.toString());
        }
        Collections.sort(result);
        return result;
    }
}
