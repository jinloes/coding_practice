package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the text, for example: aab";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.partitions("aab")).map(List::toString)
                .as("Example 1: partitions(\"aab\")").containsExactlyInAnyOrder("[a, a, b]", "[aa, b]");
        assertThat(Solution.partitions("abc")).map(List::toString)
                .as("Example 2: partitions(\"abc\")").containsExactlyInAnyOrder("[a, b, c]");
        assertThat(Solution.partitions("racecar")).map(List::toString)
                .as("Example 3: partitions(\"racecar\")").containsExactlyInAnyOrder("[r, a, c, e, c, a, r]", "[r, a, cec, a, r]", "[r, aceca, r]", "[racecar]");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty() || input.length() > 20 || input.chars().anyMatch(Character::isWhitespace)) {
            System.out.println("Give one string of 1 to 20 characters without spaces. " + USAGE);
            return;
        }
        List<List<String>> result = Solution.partitions(input);
        System.out.println("partitions(" + input + ") = " + result.size() + " partitions " + result);
    }
}
