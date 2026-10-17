package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: equal-length lowercase words separated by commas, for example: abcd,acbd,aacd";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.hasOneCharacterDifference(new String[]{"abcd", "acbd", "aacd"})).isTrue();
        assertThat(Solution.hasOneCharacterDifference(new String[]{"ab", "cd", "yz"})).isFalse();
        assertThat(Solution.hasOneCharacterDifference(new String[]{"same", "same"})).isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("No words given. " + USAGE);
            return;
        }
        String[] words = input.split(",", -1);
        int length = words[0].length();
        for (String word : words) {
            if (word.isEmpty() || word.length() != length || !word.chars().allMatch(ch -> ch >= 'a' && ch <= 'z')) {
                System.out.println("Expected non-empty equal-length lowercase words. " + USAGE);
                return;
            }
        }
        System.out.println("hasOneCharacterDifference(" + Arrays.toString(words) + ") = "
                + Solution.hasOneCharacterDifference(words));
    }
}
