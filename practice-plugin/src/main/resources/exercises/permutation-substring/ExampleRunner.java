package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the text, then the pattern, separated by a space, for example: deadbeef bee";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.containsPermutation("deadbeef", "bee")).as("Example 1: deadbeef / bee").isTrue();
        assertThat(Solution.containsPermutation("abcxyzcba", "cab")).as("Example 2: abcxyzcba / cab").isTrue();
        assertThat(Solution.containsPermutation("aabbcc", "abc")).as("Example 3: aabbcc / abc").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length != 2 || !parts[0].matches("[a-z]+") || !parts[1].matches("[a-z]+")) {
            System.out.println("Could not read \"" + input + "\" as two lowercase words. " + USAGE);
            return;
        }
        System.out.println("containsPermutation(\"" + parts[0] + "\", \"" + parts[1] + "\") = "
                + Solution.containsPermutation(parts[0], parts[1]));
    }
}
