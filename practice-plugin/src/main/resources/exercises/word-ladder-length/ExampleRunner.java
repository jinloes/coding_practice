package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: begin word, end word, then the word list (lowercase, equal lengths), for example: bake cave cake cave base";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.ladderLength("bake", "cave", List.of("cake", "cave", "base")))
                .as("Example 1").isEqualTo(3);
        assertThat(Solution.ladderLength("bake", "cane", List.of("cake", "base", "cave")))
                .as("Example 2").isEqualTo(0);
        assertThat(Solution.ladderLength("lead", "gold", List.of("load", "goad", "gold", "mold")))
                .as("Example 3").isEqualTo(4);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            if (tokens.length < 3) {
                throw new IllegalArgumentException();
            }
            int length = tokens[0].length();
            for (String token : tokens) {
                if (!token.matches("[a-z]+") || token.length() != length) {
                    throw new IllegalArgumentException();
                }
            }
            if (tokens[0].equals(tokens[1])) {
                throw new IllegalArgumentException();
            }
            List<String> words = Arrays.asList(tokens).subList(2, tokens.length);
            System.out.println("ladderLength(" + tokens[0] + ", " + tokens[1] + ", " + words + ") = "
                    + Solution.ladderLength(tokens[0], tokens[1], words));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
