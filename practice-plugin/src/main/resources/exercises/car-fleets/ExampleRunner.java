package com.jinloes.practice;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the target, then the positions, then the speeds, for example: 100 [10, 40, 70] [5, 3, 1]";

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
        check(100, new int[]{10, 40, 70}, new int[]{5, 3, 1}, 1);
    }

    private static void example2() {
        check(50, new int[]{0, 10, 20}, new int[]{1, 2, 4}, 3);
    }

    private static void example3() {
        check(12, new int[]{3}, new int[]{2}, 1);
    }

    private static void check(int target, int[] positions, int[] speeds, int expected) {
        int[] originalPositions = positions.clone();
        int[] originalSpeeds = speeds.clone();
        String call = "countFleets(%d, %s, %s)".formatted(target, Arrays.toString(originalPositions), Arrays.toString(originalSpeeds));
        assertThat(Solution.countFleets(target, positions, speeds)).as(call).isEqualTo(expected);
        assertThat(positions).as(call + " must not change positions").containsExactly(originalPositions);
        assertThat(speeds).as(call + " must not change speeds").containsExactly(originalSpeeds);
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
        int target;
        int[] positions;
        int[] speeds;
        try {
            Matcher matcher = Pattern.compile("^(-?\\d+)\\s+(\\[[^\\]]*\\])\\s+(\\[[^\\]]*\\])$").matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("shape");
            }
            target = Integer.parseInt(matcher.group(1));
            positions = parseInts(matcher.group(2));
            speeds = parseInts(matcher.group(3));
            if (positions.length != speeds.length) {
                throw new IllegalArgumentException("lengths");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("countFleets(" + target + ", " + Arrays.toString(positions) + ", " + Arrays.toString(speeds)
                + ") = " + Solution.countFleets(target, positions, speeds));
    }
}
