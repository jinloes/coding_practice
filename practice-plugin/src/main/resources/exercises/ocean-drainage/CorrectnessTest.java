package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridHasNoCells() {
        assertThat(Solution.drainageCells(new int[0][0])).isEmpty();
        assertThat(Solution.drainageCells(new int[1][0])).isEmpty();
    }

    @Test
    void singleColumnTouchesBothOceans() {
        assertThat(cells(new int[][]{{3}, {1}, {2}})).containsExactlyInAnyOrder("0,0", "1,0", "2,0");
    }

    @Test
    void identicalHeightsReachBothOceansEverywhere() {
        assertThat(cells(new int[][]{{4, 4, 4}, {4, 4, 4}, {4, 4, 4}})).hasSize(9);
    }

    @Test
    void ascendingTowardSouthEastOnlyEdgesQualify() {
        assertThat(cells(new int[][]{{0, 1, 2, 3}, {1, 2, 3, 4}, {2, 3, 4, 5}}))
                .containsExactlyInAnyOrder("0,3", "1,3", "2,3", "2,0", "2,1", "2,2");
    }

    @Test
    void sunkenCenterIsExcluded() {
        assertThat(cells(new int[][]{{9, 9, 9}, {9, 1, 9}, {9, 9, 9}}))
                .containsExactlyInAnyOrder("0,0", "0,1", "0,2", "1,0", "1,2", "2,0", "2,1", "2,2");
    }

    @Test
    void highPlateauInsideALowRing() {
        assertThat(cells(new int[][]{{1, 1, 1, 1}, {1, 9, 9, 1}, {1, 9, 9, 1}, {1, 1, 1, 1}}))
                .containsExactlyInAnyOrder("0,0", "0,1", "0,2", "0,3", "1,0", "1,1", "1,2", "1,3",
                        "2,0", "2,1", "2,2", "2,3", "3,0", "3,1", "3,2", "3,3");
    }

    @Test
    void waterCannotClimbOutOfAPocket() {
        assertThat(cells(new int[][]{{5, 1, 5}, {1, 0, 1}, {5, 1, 5}}))
                .containsExactlyInAnyOrder("0,2", "2,0");
    }

    @Test
    void handlesNegativeAndLargeHeights() {
        assertThat(cells(new int[][]{{Integer.MIN_VALUE, Integer.MAX_VALUE}, {-1, 0}}))
                .containsExactlyInAnyOrder("0,1", "1,0", "1,1");
    }

    @Test
    void doesNotModifyTheGridAndReturnsCoordinatePairs() {
        int[][] grid = {{1, 2}, {2, 1}};
        List<int[]> result = Solution.drainageCells(grid);
        assertThat(grid).isDeepEqualTo(new int[][]{{1, 2}, {2, 1}});
        assertThat(result).allSatisfy(cell -> assertThat(cell).hasSize(2));
    }

    @Test
    void handlesLargeMonotonicGridWithoutDeepRecursion() {
        int size = 300;
        int[][] grid = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = size * size - (r * size + c);
            }
        }
        assertThat(Solution.drainageCells(grid)).hasSize(2 * size - 1);
    }

    private static List<String> cells(int[][] heights) {
        List<String> result = new ArrayList<>();
        for (int[] cell : Solution.drainageCells(heights)) {
            result.add(cell[0] + "," + cell[1]);
        }
        return result;
    }
}
