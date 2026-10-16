package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the daily prices, for example: [1, 2, 3, 0, 2]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.maxProfit(new int[]{1, 2, 3, 0, 2})).as("maxProfit([1, 2, 3, 0, 2])").isEqualTo(3);
        assertThat(Solution.maxProfit(new int[]{5, 4, 3})).as("maxProfit([5, 4, 3])").isEqualTo(0);
        assertThat(Solution.maxProfit(new int[]{1, 4, 2, 7})).as("maxProfit([1, 4, 2, 7])").isEqualTo(6);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] prices;
        try {
            prices = parseInts(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("maxProfit(" + Arrays.toString(prices) + ")) = " + Solution.maxProfit(prices));
    }

    private static int[] parseInts(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        String[] tokens = cleaned.split("\\s+");
        int[] prices = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            prices[i] = Integer.parseInt(tokens[i]);
        }
        return prices;
    }
}
