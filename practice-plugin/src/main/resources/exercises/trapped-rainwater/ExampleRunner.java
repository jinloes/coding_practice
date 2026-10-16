package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the bar heights, for example: [0, 2, 0, 3, 1, 0, 2]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.trap(new int[]{0, 2, 0, 3, 1, 0, 2})).as("Example 1: trap([0, 2, 0, 3, 1, 0, 2])").isEqualTo(5);
        assertThat(Solution.trap(new int[]{3, 1, 3})).as("Example 2: trap([3, 1, 3])").isEqualTo(2);
        assertThat(Solution.trap(new int[]{1, 2, 3})).as("Example 3: trap([1, 2, 3])").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] heights;
        try {
            heights = parseInts(input);
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("trap(" + Arrays.toString(heights) + ") = " + Solution.trap(heights));
    }

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
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
