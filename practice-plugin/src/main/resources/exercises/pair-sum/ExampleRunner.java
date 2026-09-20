package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values followed by the target, for example: [2, 7, 11, 15] 9";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertPair(new int[]{2, 7, 11, 15}, 9);
        assertPair(new int[]{3, 3}, 6);
        assertThat(Solution.findPair(new int[]{1, 2, 3}, 7))
                .as("Example 3: findPair([1, 2, 3], 7) must report that no pair exists")
                .isEmpty();
        System.out.println("Examples passed: 3");
    }

    private static void assertPair(int[] numbers, int target) {
        String call = "findPair(%s, %d)".formatted(Arrays.toString(numbers), target);
        int[] pair = Solution.findPair(numbers, target);
        assertThat(pair).as("%s must return exactly two indices", call).hasSize(2);
        assertThat(pair[0]).as("%s returned first index %d", call, pair[0])
                .isBetween(0, numbers.length - 1);
        assertThat(pair[1]).as("%s returned second index %d", call, pair[1])
                .isBetween(0, numbers.length - 1)
                .isNotEqualTo(pair[0]);
        assertThat((long) numbers[pair[0]] + numbers[pair[1]])
                .as("%s returned indices %s whose values must add to the target",
                        call, Arrays.toString(pair))
                .isEqualTo(target);
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
        int[] numbers = Arrays.copyOf(values, values.length - 1);
        int[] pair = Solution.findPair(numbers, target);
        System.out.println("findPair(" + Arrays.toString(numbers) + ", " + target + ") = "
                + Arrays.toString(pair));
        if (pair.length == 2 && pair[0] >= 0 && pair[0] < numbers.length
                && pair[1] >= 0 && pair[1] < numbers.length) {
            System.out.println("  values " + numbers[pair[0]] + " + " + numbers[pair[1]] + " = "
                    + ((long) numbers[pair[0]] + numbers[pair[1]]));
        } else if (pair.length == 0) {
            System.out.println("  reported that no pair adds to " + target);
        }
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
