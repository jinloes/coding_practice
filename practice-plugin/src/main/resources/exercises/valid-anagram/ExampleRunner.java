package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the two words separated by a space, for example: listen silent";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isAnagram("listen", "silent"))
                .as("Example 1: isAnagram(\"listen\", \"silent\")").isTrue();
        assertThat(Solution.isAnagram("rat", "car"))
                .as("Example 2: isAnagram(\"rat\", \"car\")").isFalse();
        assertThat(Solution.isAnagram("aacc", "ccac"))
                .as("Example 3: isAnagram(\"aacc\", \"ccac\")").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] words = input.replace(',', ' ').replace('"', ' ').trim().split("\\s+");
        if (words.length != 2) {
            System.out.println("Expected exactly two words but read " + words.length + ". " + USAGE);
            return;
        }
        for (String word : words) {
            if (!word.chars().allMatch(character -> character >= 'a' && character <= 'z')) {
                System.out.println("The contract allows only the lowercase letters a-z, but \"" + word
                        + "\" contains something else. " + USAGE);
                return;
            }
        }
        System.out.println("isAnagram(\"" + words[0] + "\", \"" + words[1] + "\") = "
                + Solution.isAnagram(words[0], words[1]));
    }
}
