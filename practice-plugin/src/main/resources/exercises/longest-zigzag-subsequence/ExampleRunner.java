package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: integer values, for example: [1, 7, 4, 9, 2, 5]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.longestZigZag(new int[]{1, 7, 4, 9, 2, 5})).isEqualTo(6);
        assertThat(Solution.longestZigZag(new int[]{1, 1, 1})).isEqualTo(1);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.longestZigZag(parseInts(input))); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        return Arrays.stream(cleaned.split("\s+")).mapToInt(Integer::parseInt).toArray();
    }
}
