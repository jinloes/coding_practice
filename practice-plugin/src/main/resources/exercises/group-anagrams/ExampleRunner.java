package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: lowercase words separated by commas with no spaces, for example: stop,pots,cat,tops,act";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        assertGroups(List.of("stop", "pots", "cat", "tops", "act"),
                List.of(List.of("stop", "pots", "tops"), List.of("cat", "act")));
        assertGroups(List.of("", ""), List.of(List.of("", "")));
        assertGroups(List.of("abc"), List.of(List.of("abc")));
        System.out.println("Examples passed: 3");
    }

    private static void assertGroups(List<String> words, List<List<String>> expected) {
        assertThat(normalize(Solution.groupAnagrams(new ArrayList<>(words))))
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

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("No words given. " + USAGE);
            return;
        }
        List<String> words = List.of(input.split(",", -1));
        for (String word : words) {
            if (!word.chars().allMatch(character -> character >= 'a' && character <= 'z')) {
                System.out.println("The contract allows only the lowercase letters a-z, but \"" + word
                        + "\" contains something else. " + USAGE);
                return;
            }
        }
        System.out.println("groupAnagrams(" + words + ") = " + Solution.groupAnagrams(new ArrayList<>(words)));
    }
}
