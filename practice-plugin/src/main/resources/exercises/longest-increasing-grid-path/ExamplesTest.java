package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void windingPathVisitsEveryCell() {
        assertThat(Solution.longestRise(new int[][]{{3, 4, 5}, {2, 9, 6}, {1, 8, 7}})).as("longestRise([[3, 4, 5], [2, 9, 6], [1, 8, 7]])").isEqualTo(9);
    }

    @Test
    void equalValuesGiveSingleCell() {
        assertThat(Solution.longestRise(new int[][]{{5, 5}, {5, 5}})).as("longestRise([[5, 5], [5, 5]])").isEqualTo(1);
    }

    @Test
    void singleRowPath() {
        assertThat(Solution.longestRise(new int[][]{{1, 4, 2, 8}})).as("longestRise([[1, 4, 2, 8]])").isEqualTo(2);
    }
}
