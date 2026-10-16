package com.jinloes.practice;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the first sorted array, then the second, for example: [1, 3] [2]";

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
        check(new int[]{1, 3}, new int[]{2}, 2.0);
    }

    private static void example2() {
        check(new int[]{1, 2}, new int[]{3, 4}, 2.5);
    }

    private static void example3() {
        check(new int[]{}, new int[]{5}, 5.0);
    }

    private static void check(int[] first, int[] second, double expected) {
        String call = "median(%s, %s)".formatted(Arrays.toString(first), Arrays.toString(second));
        int[] firstCopy = first.clone();
        int[] secondCopy = second.clone();
        assertThat(Solution.median(first, second)).as(call).isEqualTo(expected);
        assertThat(Solution.median(second, first)).as(call + " with the arrays swapped").isEqualTo(expected);
        assertThat(first).as(call + " must not change first").containsExactly(firstCopy);
        assertThat(second).as(call + " must not change second").containsExactly(secondCopy);
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
        int[] first;
        int[] second;
        try {
            Matcher matcher = Pattern.compile("^(\\[[^\\]]*\\])\\s*(\\[[^\\]]*\\])$").matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("shape");
            }
            first = parseInts(matcher.group(1));
            second = parseInts(matcher.group(2));
            if (first.length + second.length == 0) {
                throw new IllegalArgumentException("empty");
            }
            for (int[] array : new int[][] {first, second}) {
                for (int i = 1; i < array.length; i++) {
                    if (array[i] < array[i - 1]) {
                        throw new IllegalArgumentException("unsorted");
                    }
                }
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("median(" + Arrays.toString(first) + ", " + Arrays.toString(second) + ") = "
                + Solution.median(first, second));
    }
}
