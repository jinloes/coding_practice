package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: daily prices, for example: [3, 3, 5, 0, 0, 3, 1, 4]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4})).isEqualTo(6);
        assertThat(Solution.maxProfit(new int[]{7, 6, 4, 3, 1})).isZero();
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.maxProfit(parseInts(input))); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        return Arrays.stream(cleaned.split("\s+")).mapToInt(Integer::parseInt).toArray();
    }
}
