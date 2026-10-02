package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the coin values followed by the amount, for example: [1, 3, 4] 6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minCoins(new int[]{1, 3, 4}, 6)).as("Example 1: minCoins([1, 3, 4], 6)").isEqualTo(2);
        assertThat(Solution.minCoins(new int[]{5, 10}, 3)).as("Example 2: minCoins([5, 10], 3)").isEqualTo(-1);
        assertThat(Solution.minCoins(new int[]{7}, 0)).as("Example 3: minCoins([7], 0)").isEqualTo(0);
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
            System.out.println("No amount given. " + USAGE);
            return;
        }
        int amount = values[values.length - 1];
        int[] coins = Arrays.copyOf(values, values.length - 1);
        if (amount < 0 || Arrays.stream(coins).anyMatch(coin -> coin <= 0)) {
            System.out.println("Coins must be positive and the amount cannot be negative. " + USAGE);
            return;
        }
        System.out.println("minCoins(" + Arrays.toString(coins) + ", " + amount + ") = "
                + Solution.minCoins(coins, amount));
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
