package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values as a list, for example: [1, 2, 2]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(show(Solution.subsets(new int[]{1, 2, 2})))
                .as("Example 1: subsets([1, 2, 2])").containsExactlyInAnyOrder("[1, 2, 2]", "[1, 2]", "[1]", "[2, 2]", "[2]", "[]");
        assertThat(show(Solution.subsets(new int[]{4, 4, 4})))
                .as("Example 2: subsets([4, 4, 4])").containsExactlyInAnyOrder("[4, 4, 4]", "[4, 4]", "[4]", "[]");
        assertThat(show(Solution.subsets(new int[]{0})))
                .as("Example 3: subsets([0])").containsExactlyInAnyOrder("[0]", "[]");
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
        if (values.length > 12) {
            System.out.println("Use at most 12 values. " + USAGE);
            return;
        }
        List<String> result = show(Solution.subsets(values));
        System.out.println("subsets(" + Arrays.toString(values) + ") = " + result.size() + " results " + result);
    }

    private static List<String> show(List<List<Integer>> lists) {
        List<String> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            List<Integer> sorted = new ArrayList<>(list);
            Collections.sort(sorted);
            result.add(sorted.toString());
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
