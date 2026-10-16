package com.jinloes.practice;


import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: intervals, a bar, then queries, for example: [[2,5],[3,4],[8,9]] | [4,6,9,1]";

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
        check(new int[][]{{2, 5}, {3, 4}, {8, 9}}, new int[]{4, 6, 9, 1}, 2, -1, 2, -1);
    }

    private static void example2() {
        check(new int[][]{{1, 10}}, new int[]{1, 10, 11}, 10, 10, -1);
    }

    private static void example3() {
        check(new int[][]{}, new int[]{5}, -1);
    }

    private static void check(int[][] intervals, int[] queries, int... expected) {
        String call = "smallestCovering(%s, %s)".formatted(Arrays.deepToString(intervals), Arrays.toString(queries));
        assertThat(Solution.smallestCovering(intervals, queries)).as(call).containsExactly(expected);
    }

    private static int[] bruteForce(int[][] intervals, int[] queries) {
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int best = -1;
            for (int[] interval : intervals) {
                int size = interval[1] - interval[0] + 1;
                if (interval[0] <= queries[i] && queries[i] <= interval[1] && (best == -1 || size < best)) {
                    best = size;
                }
            }
            answers[i] = best;
        }
        return answers;
    }

    private static void runInput(String input) {
        try {
            String[] parts = input.split("\\|", -1);
            if (parts.length != 2 || !input.matches("[\\[\\]\\d,\\s|]*")) {
                throw new IllegalArgumentException("shape");
            }
            int[] flat = parseInts(parts[0]);
            if (flat.length % 2 != 0) {
                throw new IllegalArgumentException("odd interval values");
            }
            int[][] intervals = new int[flat.length / 2][];
            for (int i = 0; i < intervals.length; i++) {
                intervals[i] = new int[]{flat[2 * i], flat[2 * i + 1]};
            }
            int[] queries = parseInts(parts[1]);
            System.out.println("smallestCovering(" + Arrays.deepToString(intervals) + ", " + Arrays.toString(queries)
                    + ") = " + Arrays.toString(Solution.smallestCovering(intervals, queries)));
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
        }
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
}
