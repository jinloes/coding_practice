package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, for example: [-4, 1, 3, 0, 4, -3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{-4, 1, 3, 0, 4, -3}, new int[]{-4, 1, 3}, new int[]{-4, 0, 4}, new int[]{-3, 0, 3});
        check(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0});
        check(new int[]{2, 5, 9});
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] numbers, int[]... expected) {
        String call = "findTriplets(%s)".formatted(Arrays.toString(numbers));
        List<List<Integer>> actual = Solution.findTriplets(numbers);
        assertThat(actual).as(call).isNotNull();
        assertThat(canon(actual)).as(call + " canonical triplets").isEqualTo(canon(expected));
    }

    private static List<String> canon(List<List<Integer>> triplets) {
        return triplets.stream()
                .map(triplet -> triplet.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")))
                .sorted()
                .toList();
    }

    private static List<String> canon(int[][] triplets) {
        return Arrays.stream(triplets)
                .map(triplet -> Arrays.stream(triplet).sorted().mapToObj(String::valueOf).collect(Collectors.joining(",")))
                .sorted()
                .toList();
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("findTriplets(" + Arrays.toString(numbers) + ") = " + Solution.findTriplets(numbers));
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
