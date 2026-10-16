package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: n+1 integers from 1..n with exactly one repeated value, for example: 3 1 3 4 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.findDuplicate(new int[]{1, 3, 4, 2, 2})).as("Example 1").isEqualTo(2);
        assertThat(Solution.findDuplicate(new int[]{3, 1, 3, 4, 2})).as("Example 2").isEqualTo(3);
        assertThat(Solution.findDuplicate(new int[]{2, 2, 2, 2, 2})).as("Example 3").isEqualTo(2);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            int[] nums = Arrays.stream(input.split("\\s+")).mapToInt(Integer::parseInt).toArray();
            int n = nums.length - 1;
            int[] counts = new int[Math.max(n, 0) + 1];
            for (int value : nums) {
                if (n < 1 || value < 1 || value > n) {
                    System.out.println("Values must be between 1 and " + n + ". " + USAGE);
                    return;
                }
                counts[value]++;
            }
            long repeated = Arrays.stream(counts).filter(count -> count > 1).count();
            if (repeated != 1) {
                System.out.println("Exactly one value must repeat. " + USAGE);
                return;
            }
            System.out.println("findDuplicate = " + Solution.findDuplicate(nums));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
