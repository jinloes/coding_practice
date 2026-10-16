package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: three words (first second merged), for example: ab ac acab";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isInterleaving("ab", "ac", "acab")).as("isInterleaving(\"ab\", \"ac\", \"acab\")").isEqualTo(true);
        assertThat(Solution.isInterleaving("ab", "cd", "acbe")).as("isInterleaving(\"ab\", \"cd\", \"acbe\")").isEqualTo(false);
        assertThat(Solution.isInterleaving("", "xyz", "xyz")).as("isInterleaving(\"\", \"xyz\", \"xyz\")").isEqualTo(true);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String first;
        String second;
        String merged;
        try {
            String[] words = input.trim().split("\\s+");
            if (words.length != 3) {
                throw new IllegalArgumentException("need three words");
            }
            first = words[0];
            second = words[1];
            merged = words[2];
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("isInterleaving(\"" + first + "\", \"" + second + "\", \"" + merged + "\") = " + Solution.isInterleaving(first, second, merged));
    }
}
