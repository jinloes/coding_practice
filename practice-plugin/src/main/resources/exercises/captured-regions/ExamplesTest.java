package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void flipsTheEnclosedPocket() {
        check(new String[]{"XXXX", "XXXX", "XXXX", "XOXX"}, "XXXX", "XOOX", "XXOX", "XOXX");
    }

    @Test
    void keepsRegionsThatTouchTheBorder() {
        check(new String[]{"OOO", "OXO", "OOO"}, "OOO", "OXO", "OOO");
    }

    @Test
    void flipsSeveralSeparatePockets() {
        check(new String[]{"XXXXX", "XXXXX", "XXOXX"}, "XXXXX", "XOXOX", "XXOXX");
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
