package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the candidates followed by the target, for example: [4, 1, 1, 2, 3] 5";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(show(Solution.combinations(new int[]{4, 1, 1, 2, 3}, 5)))
                .as("Example 1: combinations([4, 1, 1, 2, 3], 5)").containsExactlyInAnyOrder("[1, 1, 3]", "[1, 4]", "[2, 3]");
        assertThat(show(Solution.combinations(new int[]{2, 2, 2}, 4)))
                .as("Example 2: combinations([2, 2, 2], 4)").containsExactlyInAnyOrder("[2, 2]");
        assertThat(Solution.combinations(new int[]{3, 5}, 4))
                .as("Example 3: combinations([3, 5], 4)").isEmpty();
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
        if (values.length == 0) {
            System.out.println("No target given. " + USAGE);
            return;
        }
        int target = values[values.length - 1];
        int[] candidates = Arrays.copyOf(values, values.length - 1);
        if (target <= 0 || Arrays.stream(candidates).anyMatch(value -> value <= 0)) {
            System.out.println("Candidates and the target must be positive. " + USAGE);
            return;
        }
        List<String> result = show(Solution.combinations(candidates, target));
        System.out.println("combinations(" + Arrays.toString(candidates) + ", " + target + ") = " + result);
    }

    private static List<String> show(List<List<Integer>> combinations) {
        List<String> result = new ArrayList<>();
        for (List<Integer> combination : combinations) {
            List<Integer> sorted = new ArrayList<>(combination);
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
