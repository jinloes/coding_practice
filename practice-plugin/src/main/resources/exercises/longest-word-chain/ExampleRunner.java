package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: comma-separated words, for example: a, at, cat, chat, chart, dog";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.longestChain(new String[]{"a", "at", "cat", "chat", "chart", "dog"}))
                .as("Example 1").isEqualTo(5);
        assertThat(solution.longestChain(new String[]{"spline", "in", "spine", "spin", "pin", "on"}))
                .as("Example 2").isEqualTo(5);
        assertThat(solution.longestChain(new String[]{"team", "jira"}))
                .as("Example 3").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("Empty input. " + USAGE);
            return;
        }
        String[] parts = input.split(",", -1);
        String[] words = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String word = parts[i].trim();
            if (word.isEmpty() || !word.matches("[a-z]+")) {
                System.out.println("Invalid word '" + word + "'. " + USAGE);
                return;
            }
            words[i] = word;
        }
        System.out.println(new Solution().longestChain(words));
    }
}
