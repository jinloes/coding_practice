package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values followed by k, for example: [1, 1, 1, 2, 2, 3] 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2))
                .as("Example 1: the two most frequent values").containsExactlyInAnyOrder(1, 2);
        assertThat(Solution.topKFrequent(new int[]{5}, 1))
                .as("Example 2: a single value").containsExactly(5);
        assertThat(Solution.topKFrequent(new int[]{4, 4, -1, -1, -1, 7}, 1))
                .as("Example 3: negative values count too").containsExactly(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int close = input.indexOf(']');
        if (!input.startsWith("[") || close < 0) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        int[] values;
        int k;
        try {
            values = parseInts(input.substring(1, close));
            k = Integer.parseInt(input.substring(close + 1).trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (k < 1 || Arrays.stream(values).distinct().count() < k) {
            System.out.println("k must be between 1 and the number of distinct values. " + USAGE);
            return;
        }
        int[] result = Solution.topKFrequent(values, k);
        int[] sorted = result.clone();
        Arrays.sort(sorted);
        System.out.println("topKFrequent(" + input + ") = " + Arrays.toString(sorted));
    }

    private static int[] parseInts(String text) {
        String cleaned = text.replace(',', ' ').trim();
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
