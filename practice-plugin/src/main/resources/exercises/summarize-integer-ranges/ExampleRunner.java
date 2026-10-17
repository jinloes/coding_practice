package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: sorted distinct integers, for example: [0, 1, 2, 4, 5, 7]";

    private ExampleRunner() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.summarize(new int[]{0, 1, 2, 4, 5, 7}))
                .containsExactly("0-2", "4-5", "7");
        assertThat(Solution.summarize(new int[]{-3, -2, 0, 2, 3, 4}))
                .containsExactly("-3--2", "0", "2-4");
        System.out.println("Examples passed: 2");
    }

    private static void runInput(String input) {
        try {
            System.out.println(Solution.summarize(parseInts(input)));
        } catch (NumberFormatException exception) {
            System.out.println("Could not read input. " + USAGE);
        }
    }

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        return Arrays.stream(cleaned.split("\s+")).mapToInt(Integer::parseInt).toArray();
    }
}
