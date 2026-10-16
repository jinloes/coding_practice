package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: k, the starting values and the values to add, separated by semicolons, for example: 2; 10 3 7; 4 12 8 15";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution(2, new int[]{10, 3, 7});
        assertThat(first.add(4)).as("Example 1: add(4)").isEqualTo(7);
        assertThat(first.add(12)).as("Example 1: add(12)").isEqualTo(10);
        assertThat(first.add(8)).as("Example 1: add(8)").isEqualTo(10);
        assertThat(first.add(15)).as("Example 1: add(15)").isEqualTo(12);

        Solution second = new Solution(1, new int[]{});
        assertThat(second.add(-5)).as("Example 2: add(-5)").isEqualTo(-5);
        assertThat(second.add(-9)).as("Example 2: add(-9)").isEqualTo(-5);
        assertThat(second.add(2)).as("Example 2: add(2)").isEqualTo(2);

        Solution third = new Solution(2, new int[]{6, 6});
        assertThat(third.add(6)).as("Example 3: add(6)").isEqualTo(6);
        assertThat(third.add(1)).as("Example 3: add(1)").isEqualTo(6);
        assertThat(third.add(9)).as("Example 3: add(9)").isEqualTo(6);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int k;
        int[] initial;
        int[] adds;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException("expected three parts");
            }
            k = Integer.parseInt(parts[0].trim());
            initial = numbers(parts[1]);
            adds = numbers(parts[2]);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        Solution stream;
        try {
            stream = new Solution(k, initial);
        } catch (IllegalArgumentException exception) {
            System.out.println("Rejected: " + exception.getMessage());
            return;
        }
        List<Integer> answers = new ArrayList<>();
        for (int value : adds) {
            answers.add(stream.add(value));
        }
        System.out.println("kth largest after each add = " + answers);
    }

    private static int[] numbers(String text) {
        String trimmed = text.replace(',', ' ').trim();
        if (trimmed.isEmpty()) {
            return new int[0];
        }
        String[] tokens = trimmed.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }
}
