package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsTwoSeparateGroups() {
        assertThat(Solution.countIslands(grid("110", "010", "001"))).isEqualTo(2);
    }

    @Test
    void waterOnlyGridHasNoIslands() {
        assertThat(Solution.countIslands(grid("000", "000"))).isZero();
    }

    @Test
    void diagonalLandIsNotConnected() {
        assertThat(Solution.countIslands(grid("101", "010", "101"))).isEqualTo(5);
    }

    private static char[][] grid(String... rows) {
        char[][] cells = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].toCharArray();
        }
        return cells;
    }
}
