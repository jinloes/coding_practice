package com.jinloes.practice;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the pile sizes, then the hours, for example: [4, 9, 11] 5";

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
        check(new int[]{4, 9, 11}, 5, 6);
    }

    private static void example2() {
        check(new int[]{10, 10}, 2, 10);
    }

    private static void example3() {
        check(new int[]{7, 1, 3}, 100, 1);
    }

    private static void check(int[] piles, int hours, int expected) {
        int[] original = piles.clone();
        String call = "minimumSpeed(%s, %d)".formatted(Arrays.toString(original), hours);
        assertThat(Solution.minimumSpeed(piles, hours)).as(call).isEqualTo(expected);
        assertThat(piles).as(call + " must not change the input").containsExactly(original);
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
        int[] piles;
        int hours;
        try {
            Matcher matcher = Pattern.compile("^(\\[[^\\]]*\\])\\s+(-?\\d+)$").matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("shape");
            }
            piles = parseInts(matcher.group(1));
            hours = Integer.parseInt(matcher.group(2));
            if (piles.length == 0 || hours < piles.length || Arrays.stream(piles).anyMatch(pile -> pile < 1)) {
                throw new IllegalArgumentException("contract");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("minimumSpeed(" + Arrays.toString(piles) + ", " + hours + ") = "
                + Solution.minimumSpeed(piles, hours));
    }
}
