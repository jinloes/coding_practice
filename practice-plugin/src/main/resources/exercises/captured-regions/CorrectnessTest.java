package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyBoardIsLeftAlone() {
        char[][] board = new char[0][];
        Solution.flipEnclosed(board);
        assertThat(board).as("empty board").isEmpty();
    }

    @Test
    void singleCellOnTheBorderStays() {
        check(new String[]{"O"}, "O");
        check(new String[]{"X"}, "X");
    }

    @Test
    void regionTouchingTheLeftBorderStays() {
        check(new String[]{"XXXX", "OOXX", "XXXX"}, "XXXX", "OOXX", "XXXX");
    }

    @Test
    void regionTouchingTheRightBorderStays() {
        check(new String[]{"XXXX", "XXOO", "XXXX"}, "XXXX", "XXOO", "XXXX");
    }

    @Test
    void diagonalContactDoesNotConnect() {
        check(new String[]{"OXX", "XXX", "XXX"}, "OXX", "XOX", "XXX");
    }

    @Test
    void longTunnelFromTheBorderKeepsEveryCell() {
        check(new String[]{"XXXXX", "OOOOX", "XXXOX", "XXXOX", "XXXXX"}, "XXXXX", "OOOOX", "XXXOX", "XXXOX", "XXXXX");
    }

    @Test
    void pocketNextToTunnelStillFlips() {
        check(new String[]{"XXXXX", "OOXXX", "XXXXX", "XXXXX"}, "XXXXX", "OOXXX", "XXOXX", "XXXXX");
    }

    @Test
    void fullyOpenLargeBoardKeepsEveryCell() {
        String[] rows = repeat("O".repeat(30), 30);
        check(rows.clone(), rows);
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
}
