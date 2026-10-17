package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the values as an array, for example: [1, 1, 2]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.uniquePermutations(new int[]{1, 1, 2})).containsExactlyInAnyOrder(List.of(1, 1, 2), List.of(1, 2, 1), List.of(2, 1, 1));
        assertThat(Solution.uniquePermutations(new int[]{})).containsExactly(List.of());
        assertThat(Solution.uniquePermutations(new int[]{3, 3, 3})).containsExactly(List.of(3, 3, 3));
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] values = parseInts(input); System.out.println("uniquePermutations(" + Arrays.toString(values) + ") = " + Solution.uniquePermutations(values)); }
        catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        String[] parts = cleaned.split("\\s+"); int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) values[i] = Integer.parseInt(parts[i]);
        return values;
    }
}
