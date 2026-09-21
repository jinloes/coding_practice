package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one lowercase word, for example: loveleetcode";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.firstUniqueIndex("leetcode"))
                .as("Example 1: firstUniqueIndex(\"leetcode\")").isEqualTo(0);
        assertThat(Solution.firstUniqueIndex("loveleetcode"))
                .as("Example 2: firstUniqueIndex(\"loveleetcode\")").isEqualTo(2);
        assertThat(Solution.firstUniqueIndex("aabb"))
                .as("Example 3: firstUniqueIndex(\"aabb\")").isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String word = unquote(input);
        if (!word.chars().allMatch(character -> character >= 'a' && character <= 'z')) {
            System.out.println("The contract allows one word of lowercase letters a-z, but \"" + word
                    + "\" contains something else. " + USAGE);
            return;
        }
        int index = Solution.firstUniqueIndex(word);
        System.out.println("firstUniqueIndex(\"" + word + "\") = " + index);
        if (index >= 0 && index < word.length()) {
            System.out.println("  the character at index " + index + " is '" + word.charAt(index) + "'");
        } else if (index == -1) {
            System.out.println("  reported that every character repeats");
        }
    }

    private static String unquote(String input) {
        return input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }
}
