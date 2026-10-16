package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the sorted array values, then the target, for example: [2, 5, 9, 11, 15] 13";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.findPair(new int[]{2, 5, 9, 11, 15}, 13))
                .as("Example 1: findPair([2, 5, 9, 11, 15], 13)").containsExactly(0, 3);
        assertThat(Solution.findPair(new int[]{4, 4, 9}, 8))
                .as("Example 2: findPair([4, 4, 9], 8)").containsExactly(0, 1);
        assertThat(Solution.findPair(new int[]{1, 3, 6}, 100))
                .as("Example 3: findPair([1, 3, 6], 100)").isEmpty();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] sorted;
        int target;
        try {
            int close = input.indexOf(']');
            sorted = parseInts(input.substring(0, close + 1));
            target = Integer.parseInt(input.substring(close + 1).trim());
        } catch (IllegalArgumentException | IndexOutOfBoundsException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("findPair(" + Arrays.toString(sorted) + ", " + target + ") = "
                + Arrays.toString(Solution.findPair(sorted, target)));
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
