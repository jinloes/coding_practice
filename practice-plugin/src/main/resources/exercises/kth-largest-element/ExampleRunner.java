package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: k, then the values separated by spaces or commas, for example: 2; 9 4 7 1 8";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.findKthLargest(new int[]{9, 4, 7, 1, 8}, 2)).as("Example 1").isEqualTo(8);
        assertThat(Solution.findKthLargest(new int[]{5, 5, 5, 2}, 3)).as("Example 2").isEqualTo(5);
        assertThat(Solution.findKthLargest(new int[]{-3}, 1)).as("Example 3").isEqualTo(-3);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int k;
        int[] nums;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("expected k and values");
            }
            k = Integer.parseInt(parts[0].trim());
            nums = parse(parts[1]);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        try {
            System.out.println("findKthLargest(" + Arrays.toString(nums) + ", " + k + ") = " + Solution.findKthLargest(nums, k));
        } catch (IllegalArgumentException exception) {
            System.out.println("Rejected: " + exception.getMessage());
        }
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
