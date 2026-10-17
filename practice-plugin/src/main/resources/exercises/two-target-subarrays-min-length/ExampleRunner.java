package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: integer values followed by the target, for example: [3, 2, 2, 4, 3] 3";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.minSumOfLengths(new int[]{3, 2, 2, 4, 3}, 3)).isEqualTo(2);
        assertThat(Solution.minSumOfLengths(new int[]{7, 3, 4, 7}, 7)).isEqualTo(2);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try {
            int[] values = parseInts(input);
            if (values.length == 0) { System.out.println("Missing target. " + USAGE); return; }
            int target = values[values.length - 1];
            System.out.println(Solution.minSumOfLengths(Arrays.copyOf(values, values.length - 1), target));
        } catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        return Arrays.stream(cleaned.split("\s+")).mapToInt(Integer::parseInt).toArray();
    }
}
