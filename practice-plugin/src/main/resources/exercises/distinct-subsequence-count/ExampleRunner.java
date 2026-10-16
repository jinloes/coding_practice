package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the source word and the pattern word, for example: bagbag bag";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countSubsequences("bagbag", "bag")).as("countSubsequences(\"bagbag\", \"bag\")").isEqualTo(4);
        assertThat(Solution.countSubsequences("xyz", "xzy")).as("countSubsequences(\"xyz\", \"xzy\")").isEqualTo(0);
        assertThat(Solution.countSubsequences("aaaa", "aa")).as("countSubsequences(\"aaaa\", \"aa\")").isEqualTo(6);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String source;
        String pattern;
        try {
            String[] words = input.trim().split("\\s+");
            if (words.length != 2) {
                throw new IllegalArgumentException("need two words");
            }
            source = words[0];
            pattern = words[1];
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("countSubsequences(\"" + source + "\", \"" + pattern + "\") = " + Solution.countSubsequences(source, pattern));
    }
}
