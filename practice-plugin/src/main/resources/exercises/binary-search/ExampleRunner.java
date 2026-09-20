package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the sorted values followed by the target, for example: [-4, -1, 0, 6, 9] 6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertIndex(new int[]{-4, -1, 0, 6, 9}, 6);
        assertIndex(new int[]{1, 2, 2, 2, 8}, 2);
        assertThat(Solution.search(new int[]{1, 4, 7}, 5))
                .as("Example 3: search([1, 4, 7], 5) must report the target as absent")
                .isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void assertIndex(int[] sorted, int target) {
        int index = Solution.search(sorted, target);
        assertThat(index)
                .as("search(%s, %d) must return an in-bounds index", Arrays.toString(sorted), target)
                .isBetween(0, sorted.length - 1);
        assertThat(sorted[index])
                .as("search(%s, %d) returned index %d", Arrays.toString(sorted), target, index)
                .isEqualTo(target);
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (values.length == 0) {
            System.out.println("No target given. " + USAGE);
            return;
        }
        int target = values[values.length - 1];
        int[] sorted = Arrays.copyOf(values, values.length - 1);
        int index = Solution.search(sorted, target);
        System.out.println("search(" + Arrays.toString(sorted) + ", " + target + ") = " + index);
        if (index >= 0 && index < sorted.length) {
            System.out.println("  value at index " + index + " is " + sorted[index]);
        } else if (index == -1) {
            System.out.println("  reported the target as absent");
        }
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
