package com.jinloes.practice;


import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the daily temperatures, for example: [68, 70, 65, 66, 72, 60]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        check(new int[]{68, 70, 65, 66, 72, 60}, 1, 3, 1, 1, 0, 0);
    }

    private static void example2() {
        check(new int[]{50, 50, 50}, 0, 0, 0);
    }

    private static void example3() {
        check(new int[]{60, 61, 62, 63}, 1, 1, 1, 0);
    }

    private static void check(int[] temperatures, int... expected) {
        int[] original = temperatures.clone();
        String call = "daysUntilWarmer(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.daysUntilWarmer(temperatures)).as(call).containsExactly(expected);
        assertThat(temperatures).as(call + " must not change the input").containsExactly(original);
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

    private static void runInput(String input) {
        int[] temperatures;
        try {
            temperatures = parseInts(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("daysUntilWarmer(" + Arrays.toString(temperatures) + ") = "
                + Arrays.toString(Solution.daysUntilWarmer(temperatures)));
    }
}
