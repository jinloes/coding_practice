package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the values followed by the target, for example: [1, 1, 1] 2";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.countSubarrays(new int[]{1, 1, 1}, 2)).isEqualTo(2);
        assertThat(Solution.countSubarrays(new int[]{1, -1, 0}, 0)).isEqualTo(3);
        assertThat(Solution.countSubarrays(new int[]{3, 4, 7, 2, -3, 1, 4, 2}, 7)).isEqualTo(4);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try {
            int[] values = parseInts(input);
            if (values.length < 1) { System.out.println("Need values and a target. " + USAGE); return; }
            int target = values[values.length - 1];
            int[] numbers = Arrays.copyOf(values, values.length - 1);
            System.out.println("countSubarrays(" + Arrays.toString(numbers) + ", " + target + ") = "
                    + Solution.countSubarrays(numbers, target));
        } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) values[i] = Integer.parseInt(tokens[i]);
        return values;
    }
}
