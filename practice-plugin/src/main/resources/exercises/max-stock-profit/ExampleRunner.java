package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the daily prices in day order, for example: [7, 1, 5, 3, 6, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertProfit(new int[]{7, 1, 5, 3, 6, 4}, 5);
        assertProfit(new int[]{7, 6, 4, 3, 1}, 0);
        assertProfit(new int[0], 0);
        System.out.println("Examples passed: 3");
    }

    private static void assertProfit(int[] prices, int expected) {
        assertThat(Solution.maxProfit(prices))
                .as("maxProfit(%s) must return %d", Arrays.toString(prices), expected)
                .isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[] prices;
        try {
            prices = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        int profit = Solution.maxProfit(prices);
        System.out.println("maxProfit(" + Arrays.toString(prices) + ") = " + profit);
        if (profit == 0) {
            System.out.println("  reported that no sale beats its purchase");
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
