package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the text followed by the dictionary words in brackets, for example: applepie [apple, pie]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.canSegment("applepie", List.of("apple", "pie")))
                .as("Example 1: canSegment(applepie, [apple, pie])").isTrue();
        assertThat(Solution.canSegment("abcd", List.of("a", "abc", "b", "cd")))
                .as("Example 2: canSegment(abcd, [a, abc, b, cd])").isTrue();
        assertThat(Solution.canSegment("abce", List.of("abc", "d")))
                .as("Example 3: canSegment(abce, [abc, d])").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int open = input.indexOf('[');
        int close = input.lastIndexOf(']');
        if (open <= 0 || close < open) {
            System.out.println("Expected a text followed by bracketed words. " + USAGE);
            return;
        }
        String text = input.substring(0, open).trim();
        String inside = input.substring(open + 1, close).replace(',', ' ').trim();
        List<String> words = inside.isEmpty() ? List.of() : Arrays.asList(inside.split("\\s+"));
        if (text.isEmpty() || text.contains(" ")) {
            System.out.println("Expected a single text before the words. " + USAGE);
            return;
        }
        System.out.println("canSegment(" + text + ", " + words + ") = " + Solution.canSegment(text, words));
    }
}
