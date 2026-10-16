package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values separated by spaces or commas, every value twice except one, for example: 4 9 4 7 9";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.findUnpaired(new int[]{4, 9, 4, 7, 9})).as("Example 1").isEqualTo(7);
        assertThat(Solution.findUnpaired(new int[]{42})).as("Example 2").isEqualTo(42);
        assertThat(Solution.findUnpaired(new int[]{-5, 3, -5})).as("Example 3").isEqualTo(3);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] nums;
        try {
            nums = parse(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("findUnpaired = " + Solution.findUnpaired(nums));
    }

    private static int[] parse(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("no values");
        }
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }
}
