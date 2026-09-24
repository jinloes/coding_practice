package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the words in brackets, for example: [flower, flow, flight]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.longestCommonPrefix(new String[]{"flower", "flow", "flight"}))
                .as("Example 1: longestCommonPrefix([flower, flow, flight])").isEqualTo("fl");
        assertThat(Solution.longestCommonPrefix(new String[]{"dog", "racecar", "car"}))
                .as("Example 2: longestCommonPrefix([dog, racecar, car])").isEmpty();
        assertThat(Solution.longestCommonPrefix(new String[]{"interview"}))
                .as("Example 3: longestCommonPrefix([interview])").isEqualTo("interview");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            System.out.println("Put the words between brackets, separated by commas. " + USAGE);
            return;
        }
        String inside = input.substring(1, input.length() - 1).trim();
        String[] words = inside.isEmpty() ? new String[0] : inside.split(",", -1);
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replace('"', ' ').trim();
            if (!words[i].chars().allMatch(character -> character >= 'a' && character <= 'z')) {
                System.out.println("The contract allows only the lowercase letters a-z, but \"" + words[i]
                        + "\" contains something else. " + USAGE);
                return;
            }
        }
        System.out.println("longestCommonPrefix(" + Arrays.toString(words) + ") = \""
                + Solution.longestCommonPrefix(words.clone()) + "\"");
    }
}
