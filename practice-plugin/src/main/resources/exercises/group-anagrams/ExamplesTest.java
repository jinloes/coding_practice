package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
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
