package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the board rows separated by spaces, using X and O, for example: XXXX XOOX XXOX XOXX";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new String[]{"XXXX", "XXXX", "XXXX", "XOXX"}, "XXXX", "XOOX", "XXOX", "XOXX");
        check(new String[]{"OOO", "OXO", "OOO"}, "OOO", "OXO", "OOO");
        check(new String[]{"XXXXX", "XXXXX", "XXOXX"}, "XXXXX", "XOXOX", "XXOXX");
        System.out.println("Examples passed: 3");
    }

    private static String[] flip(String... rows) {
        char[][] board = new char[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            board[r] = rows[r].toCharArray();
        }
        Solution.flipEnclosed(board);
        String[] result = new String[rows.length];
        for (int r = 0; r < rows.length; r++) {
            result[r] = new String(board[r]);
        }
        return result;
    }

    private static void check(String[] expected, String... rows) {
        assertThat(flip(rows)).as("flipEnclosed(" + Arrays.toString(rows) + ")").containsExactly(expected);
    }

    private static String[] repeat(String row, int times) {
        String[] rows = new String[times];
        Arrays.fill(rows, row);
        return rows;
    }

    private static void runInput(String input) {
        String[] rows = input.trim().split("\\s+");
        boolean valid = rows.length > 0 && !rows[0].isEmpty();
        for (String row : rows) {
            valid = valid && row.length() == rows[0].length() && row.matches("[XO]+");
        }
        if (!valid) {
            System.out.println("Could not read \"" + input + "\" as a board. " + USAGE);
            return;
        }
        System.out.println("flipEnclosed(" + Arrays.toString(rows) + ") leaves " + Arrays.toString(flip(rows)));
    }
}
