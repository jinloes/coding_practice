package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void choosesBestRightDownPath() {
        assertThat(Solution.maxPathSum(new int[][]{{5, 1, 2}, {4, 0, 3}})).isEqualTo(12);
    }
    @Test
    void handlesAllNegativeGrid() {
        assertThat(Solution.maxPathSum(new int[][]{{-1, -2}, {-3, -4}})).isEqualTo(-7);
    }
}
