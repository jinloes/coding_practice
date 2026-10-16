package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void highestCellOnTheBestRouteDecidesTheTime() {
        assertThat(Solution.swimTime(new int[][]{{4, 1, 9}, {3, 8, 2}, {7, 5, 6}}))
                .as("swimTime of a 3x3 grid")
                .isEqualTo(7);
    }

    @Test
    void singleCellCostsItsOwnElevation() {
        assertThat(Solution.swimTime(new int[][]{{5}}))
                .as("swimTime of a 1x1 grid")
                .isEqualTo(5);
    }

    @Test
    void detourAroundAWallIsCheaper() {
        assertThat(Solution.swimTime(new int[][]{{1, 8, 2}, {1, 8, 2}, {1, 1, 3}}))
                .as("swimTime of a 3x3 grid")
                .isEqualTo(3);
    }
}
