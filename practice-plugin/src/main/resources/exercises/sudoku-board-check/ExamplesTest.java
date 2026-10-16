package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsBoardWithoutRepeats() {
        char[][] board = board("1...5...9", ".5...9...", "..9...4..", "...5...9.", "5...9...4", ".9...4...", "..5...9..", "...9...4.", "9...4...8");
        assertThat(Solution.isValid(board)).as("isValid(sparse solved grid)").isTrue();
    }

    @Test
    void rejectsRepeatInsideOneBox() {
        char[][] board = board("4........", ".4.......", ".........", ".........", ".........", ".........", ".........", ".........", ".........");
        assertThat(Solution.isValid(board)).as("isValid(4 at (0,0) and (1,1))").isFalse();
    }

    @Test
    void rejectsRepeatInsideOneColumn() {
        char[][] board = board(".....7...", ".........", ".........", ".........", ".........", ".........", ".........", ".........", ".....7...");
        assertThat(Solution.isValid(board)).as("isValid(7 at (0,5) and (8,5))").isFalse();
    }

    private static char[][] board(String... rows) {
        char[][] cells = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].toCharArray();
        }
        return cells;
    }
}
