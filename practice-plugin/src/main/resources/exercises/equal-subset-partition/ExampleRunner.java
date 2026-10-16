package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, for example: [3, 1, 1, 2, 1]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.canSplitEvenly(new int[]{3, 1, 1, 2, 1})).as("canSplitEvenly([3, 1, 1, 2, 1])").isEqualTo(true);
        assertThat(Solution.canSplitEvenly(new int[]{2, 5, 4})).as("canSplitEvenly([2, 5, 4])").isEqualTo(false);
        assertThat(Solution.canSplitEvenly(new int[]{6, 6})).as("canSplitEvenly([6, 6])").isEqualTo(true);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("canSplitEvenly(" + Arrays.toString(values) + ")) = " + Solution.canSplitEvenly(values));
    }

    private static int[] parseInts(String text) {
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
