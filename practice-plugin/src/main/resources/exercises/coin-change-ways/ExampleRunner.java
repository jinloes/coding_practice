package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the amount, then the coin array, for example: 5 [1, 2, 5]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countWays(4, new int[]{1, 2, 3})).as("countWays(4, [1, 2, 3])").isEqualTo(4);
        assertThat(Solution.countWays(3, new int[]{2})).as("countWays(3, [2])").isEqualTo(0);
        assertThat(Solution.countWays(0, new int[]{5, 7})).as("countWays(0, [5, 7])").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int amount;
        int[] coins;
        try {
            String trimmed = input.trim();
            int space = trimmed.indexOf(' ');
            amount = Integer.parseInt(trimmed.substring(0, space));
            coins = parseInts(trimmed.substring(space + 1));
            if (amount < 0) {
                throw new IllegalArgumentException("negative amount");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("countWays(" + amount + ", " + Arrays.toString(coins) + ")) = " + Solution.countWays(amount, coins));
    }

    private static int[] parseInts(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
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
