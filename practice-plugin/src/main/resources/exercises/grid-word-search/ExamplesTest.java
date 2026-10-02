package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAPathThroughSeveralTurns() {
        assertThat(Solution.wordExists(grid("ABC", "SED", "TRO"), "BEST")).isTrue();
    }

    @Test
    void rejectsAWordThatNeedsACellTwice() {
        assertThat(Solution.wordExists(grid("ABC", "SED", "TRO"), "BEE")).isFalse();
    }

    @Test
    void findsAWordInASingleCell() {
        assertThat(Solution.wordExists(grid("Z"), "Z")).isTrue();
    }

    private static char[][] grid(String... rows) {
        char[][] grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        return grid;
    }
}
