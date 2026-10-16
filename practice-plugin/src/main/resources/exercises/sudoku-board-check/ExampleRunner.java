package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: nine rows of nine characters ('1'-'9' or '.') separated by spaces, for example: 1...5...9 .5...9... ..9...4.. ...5...9. 5...9...4 .9...4... ..5...9.. ...9...4. 9...4...8";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isValid(board("1...5...9", ".5...9...", "..9...4..", "...5...9.", "5...9...4", ".9...4...", "..5...9..", "...9...4.", "9...4...8")))
                .as("Example 1: sparse solved grid").isTrue();
        assertThat(Solution.isValid(board("4........", ".4.......", ".........", ".........", ".........", ".........", ".........", ".........", ".........")))
                .as("Example 2: 4 at (0,0) and (1,1)").isFalse();
        assertThat(Solution.isValid(board(".....7...", ".........", ".........", ".........", ".........", ".........", ".........", ".........", ".....7...")))
                .as("Example 3: 7 at (0,5) and (8,5)").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static char[][] board(String... rows) {
        char[][] cells = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].toCharArray();
        }
        return cells;
    }

    private static void runInput(String input) {
        char[][] board;
        try {
            board = parse(input);
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not read \"" + input + "\" as a board. " + USAGE);
            return;
        }
        System.out.println("isValid(" + Arrays.deepToString(board) + ") = " + Solution.isValid(board));
    }

    private static char[][] parse(String input) {
        String[] rows = input.replace(',', ' ').trim().split("\\s+");
        if (rows.length != 9) {
            throw new IllegalArgumentException("need nine rows");
        }
        char[][] cells = new char[9][];
        for (int r = 0; r < 9; r++) {
            if (rows[r].length() != 9) {
                throw new IllegalArgumentException("need nine cells per row");
            }
            for (char cell : rows[r].toCharArray()) {
                if (cell != '.' && (cell < '1' || cell > '9')) {
                    throw new IllegalArgumentException("bad cell");
                }
            }
            cells[r] = rows[r].toCharArray();
        }
        return cells;
    }
}
