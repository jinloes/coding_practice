package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: integer values followed by the limit, for example: [8, 2, 4, 7] 4";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.longestSubarray(new int[]{8, 2, 4, 7}, 4)).isEqualTo(2);
        assertThat(Solution.longestSubarray(new int[]{10, 1, 2, 4, 7, 2}, 5)).isEqualTo(4);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try {
            int[] values = parseInts(input);
            if (values.length == 0) { System.out.println("Missing limit. " + USAGE); return; }
            int limit = values[values.length - 1];
            int[] numbers = Arrays.copyOf(values, values.length - 1);
            System.out.println(Solution.longestSubarray(numbers, limit));
        } catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        return Arrays.stream(cleaned.split("\\s+")).mapToInt(Integer::parseInt).toArray();
    }
}
