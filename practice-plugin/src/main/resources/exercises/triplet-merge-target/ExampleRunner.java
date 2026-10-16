package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the triplets, a semicolon, then the target triplet, for example: [[2, 5, 3], [1, 8, 4], [1, 7, 5]]; [2, 7, 5]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[][]{{2, 5, 3}, {1, 8, 4}, {1, 7, 5}}, new int[]{2, 7, 5}, true);
        check(new int[][]{{3, 4, 5}, {4, 5, 6}}, new int[]{3, 4, 5}, true);
        check(new int[][]{{2, 3, 1}, {1, 2, 2}}, new int[]{2, 3, 3}, false);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[][] triplets, int[] target, boolean expected) {
        String call = "canMerge(%s, %s)".formatted(Arrays.deepToString(triplets), Arrays.toString(target));
        assertThat(Solution.canMerge(triplets, target)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[][] triplets;
        int[] target;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("triplets and target expected");
            }
            triplets = parseMatrix(parts[0]);
            target = parseInts(parts[1]);
            if (target.length != 3) {
                throw new IllegalArgumentException("target needs three values");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as triplets and a target. " + USAGE);
            return;
        }
        System.out.println("canMerge(" + Arrays.deepToString(triplets) + ", " + Arrays.toString(target) + ") = "
                + Solution.canMerge(triplets, target));
    }

    private static int[][] parseMatrix(String input) {
        String trimmed = input.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new IllegalArgumentException("brackets expected");
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return new int[0][];
        }
        List<int[]> rows = new ArrayList<>();
        for (String piece : body.split("]")) {
            String row = piece.replace(',', ' ').trim();
            if (row.isEmpty()) {
                continue;
            }
            int[] values = parseInts(row);
            if (values.length != 3) {
                throw new IllegalArgumentException("each triplet needs three values");
            }
            rows.add(values);
        }
        return rows.toArray(new int[0][]);
    }

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
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
