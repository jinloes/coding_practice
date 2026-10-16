package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values as a list, for example: [1, 2, 3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(show(Solution.permutations(new int[]{1, 2, 3})))
                .as("Example 1: permutations([1, 2, 3])").containsExactlyInAnyOrder("[1, 2, 3]", "[1, 3, 2]", "[2, 1, 3]", "[2, 3, 1]", "[3, 1, 2]", "[3, 2, 1]");
        assertThat(show(Solution.permutations(new int[]{})))
                .as("Example 2: permutations([])").containsExactlyInAnyOrder("[]");
        assertThat(show(Solution.permutations(new int[]{4, 9})))
                .as("Example 3: permutations([4, 9])").containsExactlyInAnyOrder("[4, 9]", "[9, 4]");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (values.length > 9) {
            System.out.println("Use at most 9 values. " + USAGE);
            return;
        }
        List<String> result = show(Solution.permutations(values));
        System.out.println("permutations(" + Arrays.toString(values) + ") = " + result.size() + " results " + result);
    }

    private static List<String> show(List<List<Integer>> lists) {
        List<String> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            result.add(list.toString());
        }
        return result;
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
