package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the stone weights separated by spaces or commas, for example: 8 3 5 1";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.smash(new int[]{8, 3, 5, 1})).as("Example 1").isEqualTo(1);
        assertThat(Solution.smash(new int[]{6, 6})).as("Example 2").isEqualTo(0);
        assertThat(Solution.smash(new int[]{20})).as("Example 3").isEqualTo(20);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] stones;
        try {
            stones = parse(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("smash(" + Arrays.toString(stones) + ") = " + Solution.smash(stones));
    }

    private static int[] parse(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }
}
