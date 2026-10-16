package com.jinloes.practice;


import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the bar heights, for example: [3, 1, 4, 4, 2]";

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
        check(new int[]{3, 1, 4, 4, 2}, 8);
    }

    private static void example2() {
        check(new int[]{7, 7, 7, 7}, 28);
    }

    private static void example3() {
        check(new int[]{}, 0);
    }

    private static void check(int[] heights, long expected) {
        int[] original = heights.clone();
        String call = "largestRectangle(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.largestRectangle(heights)).as(call).isEqualTo(expected);
        assertThat(heights).as(call + " must not change the input").containsExactly(original);
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
        int[] heights;
        try {
            heights = parseInts(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("largestRectangle(" + Arrays.toString(heights) + ") = " + Solution.largestRectangle(heights));
    }
}
