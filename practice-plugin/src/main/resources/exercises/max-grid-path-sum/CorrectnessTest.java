package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridHasZeroSum() { assertThat(Solution.maxPathSum(new int[0][0])).isZero(); }
    @Test
    void singleCellIsTheOnlyPath() { assertThat(Solution.maxPathSum(new int[][]{{-8}})).isEqualTo(-8); }
    @Test
    void worksForSingleRow() { assertThat(Solution.maxPathSum(new int[][]{{1, -2, 3}})).isEqualTo(2); }
    @Test
    void doesNotMutateGrid() {
        int[][] grid = {{1, 2}, {3, 4}};
        Solution.maxPathSum(grid);
        assertThat(grid).isDeepEqualTo(new int[][]{{1, 2}, {3, 4}});
    }
}
