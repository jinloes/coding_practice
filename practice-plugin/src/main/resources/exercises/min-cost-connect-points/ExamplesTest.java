package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void connectsFivePoints() {
        assertThat(Solution.minimumCost(new int[][]{{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}}))
                .as("minimumCost of 5 points")
                .isEqualTo(20);
    }

    @Test
    void singlePointIsFree() {
        assertThat(Solution.minimumCost(new int[][]{{4, 4}}))
                .as("minimumCost of 1 points")
                .isEqualTo(0);
    }

    @Test
    void twoPointsCostTheirDistance() {
        assertThat(Solution.minimumCost(new int[][]{{1, 1}, {4, 5}}))
                .as("minimumCost of 2 points")
                .isEqualTo(7);
    }
}
