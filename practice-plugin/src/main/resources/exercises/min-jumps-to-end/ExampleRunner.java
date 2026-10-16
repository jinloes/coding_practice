package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the hop lengths, where the last index is reachable, for example: [3, 1, 1, 2, 1, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{3, 1, 1, 2, 1, 4}, 2);
        check(new int[]{1, 1, 1, 1}, 3);
        check(new int[]{5}, 0);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] reach, int expected) {
        String call = "minJumps(%s)".formatted(Arrays.toString(reach));
        assertThat(Solution.minJumps(reach)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[] reach;
        try {
            reach = parseInts(input);
            if (reach.length == 0) {
                throw new IllegalArgumentException("empty");
            }
            int farthest = 0;
            for (int i = 0; i < reach.length - 1 && i <= farthest; i++) {
                if (reach[i] < 0) {
                    throw new IllegalArgumentException("negative");
                }
                farthest = Math.max(farthest, i + reach[i]);
            }
            if (farthest < reach.length - 1) {
                throw new IllegalArgumentException("unreachable");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as a reachable list of jump lengths. " + USAGE);
            return;
        }
        System.out.println("minJumps(" + Arrays.toString(reach) + ") = " + Solution.minJumps(reach));
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
