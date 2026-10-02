package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridsHaveNoIslands() {
        assertThat(Solution.countIslands(new char[0][0])).isZero();
        assertThat(Solution.countIslands(new char[2][0])).isZero();
    }

    @Test
    void singleCellGrids() {
        assertThat(Solution.countIslands(grid("1"))).isEqualTo(1);
        assertThat(Solution.countIslands(grid("0"))).isZero();
    }

    @Test
    void fullLandIsOneIsland() {
        assertThat(Solution.countIslands(grid("1111", "1111", "1111"))).isEqualTo(1);
    }

    @Test
    void uShapeJoinsLaterThanItStarts() {
        assertThat(Solution.countIslands(grid("101", "101", "111"))).isEqualTo(1);
    }

    @Test
    void spiralIsOneIsland() {
        assertThat(Solution.countIslands(grid("11111", "00001", "01101", "01001", "01111"))).isEqualTo(1);
    }

    @Test
    void ringAroundWaterAndInnerLand() {
        assertThat(Solution.countIslands(grid("11111", "10001", "10101", "10001", "11111"))).isEqualTo(2);
    }

    @Test
    void singleRowAndSingleColumn() {
        assertThat(Solution.countIslands(grid("1101011"))).isEqualTo(3);
        assertThat(Solution.countIslands(grid("1", "1", "0", "1"))).isEqualTo(2);
    }

    @Test
    void edgeTouchingGroupsAreCountedOnce() {
        assertThat(Solution.countIslands(grid("1001", "0000", "1001"))).isEqualTo(4);
        assertThat(Solution.countIslands(grid("1000", "1000", "0011"))).isEqualTo(2);
    }

    @Test
    void longSnakeIsOneIsland() {
        int size = 101;
        char[][] cells = new char[size][size];
        for (char[] row : cells) {
            Arrays.fill(row, '0');
        }
        for (int r = 0; r < size; r += 2) {
            Arrays.fill(cells[r], '1');
            if (r + 2 < size) {
                cells[r + 1][(r / 2) % 2 == 0 ? size - 1 : 0] = '1';
            }
        }
        assertThat(Solution.countIslands(cells)).isEqualTo(1);
    }

    private static char[][] grid(String... rows) {
        char[][] cells = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].toCharArray();
        }
        return cells;
    }
}
