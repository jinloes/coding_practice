package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private static final String EMPTY = ".........";

    @Test
    void acceptsAnEmptyBoard() {
        assertThat(Solution.isValid(blank())).as("isValid(empty board)").isTrue();
    }

    @Test
    void acceptsACompletedValidBoard() {
        assertThat(Solution.isValid(solved())).as("isValid(complete solved board)").isTrue();
    }

    @Test
    void rejectsRepeatInsideOneRow() {
        char[][] board = blank();
        board[4][0] = '3';
        board[4][8] = '3';
        assertThat(Solution.isValid(board)).as("same digit at both ends of row 4").isFalse();
    }

    @Test
    void rejectsRepeatThatOnlySharesABox() {
        char[][] board = blank();
        board[8][8] = '9';
        board[6][6] = '9';
        assertThat(Solution.isValid(board)).as("9 at (8,8) and (6,6), same box only").isFalse();
    }

    @Test
    void rejectsADuplicateDigitNine() {
        char[][] board = blank();
        board[2][1] = '9';
        board[2][7] = '9';
        assertThat(Solution.isValid(board)).as("two 9s in row 2").isFalse();
    }

    @Test
    void ignoresSameDigitInDifferentUnits() {
        char[][] board = blank();
        board[0][0] = '5';
        board[1][3] = '5';
        board[2][6] = '5';
        board[3][1] = '5';
        assertThat(Solution.isValid(board)).as("5s that share no row, column or box").isTrue();
    }

    @Test
    void rejectsAnAlmostSolvedBoardWithOneBadCell() {
        char[][] board = solved();
        board[0][0] = board[0][1];
        assertThat(Solution.isValid(board)).as("solved board with one cell overwritten").isFalse();
    }

    @Test
    void doesNotMutateTheBoard() {
        char[][] board = solved();
        board[3][3] = '.';
        char[][] copy = new char[9][];
        for (int i = 0; i < 9; i++) {
            copy[i] = board[i].clone();
        }
        Solution.isValid(board);
        assertThat(board).as("board after isValid").isEqualTo(copy);
    }

    private static char[][] blank() {
        char[][] cells = new char[9][];
        for (int i = 0; i < 9; i++) {
            cells[i] = EMPTY.toCharArray();
        }
        return cells;
    }

    private static char[][] solved() {
        char[][] cells = new char[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                cells[r][c] = (char) ('1' + (3 * (r % 3) + r / 3 + c) % 9);
            }
        }
        return cells;
    }
}
