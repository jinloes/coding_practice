package com.jinloes.practice;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the rows, then the target, for example: [[1, 4, 7], [9, 12, 15], [20, 22, 30]] 12";

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
        check(new int[][]{{1, 4, 7}, {9, 12, 15}, {20, 22, 30}}, 12, true);
    }

    private static void example2() {
        check(new int[][]{{1, 4, 7}, {9, 12, 15}, {20, 22, 30}}, 10, false);
    }

    private static void example3() {
        check(new int[][]{{5}}, 5, true);
    }

    private static void check(int[][] grid, int target, boolean expected) {
        String call = "containsValue(%s, %d)".formatted(Arrays.deepToString(grid), target);
        assertThat(Solution.containsValue(grid, target)).as(call).isEqualTo(expected);
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
        int[][] grid;
        int target;
        try {
            int end = input.lastIndexOf(']');
            String gridText = input.substring(0, end + 1);
            if (!gridText.startsWith("[")) {
                throw new IllegalArgumentException("grid");
            }
            target = Integer.parseInt(input.substring(end + 1).trim());
            Matcher matcher = Pattern.compile("\\[([^\\[\\]]*)\\]").matcher(gridText.substring(1, gridText.length() - 1));
            java.util.List<int[]> rows = new java.util.ArrayList<>();
            while (matcher.find()) {
                rows.add(parseInts(matcher.group(1)));
            }
            grid = rows.toArray(new int[0][]);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("containsValue(" + Arrays.deepToString(grid) + ", " + target + ") = "
                + Solution.containsValue(grid, target));
    }
}
