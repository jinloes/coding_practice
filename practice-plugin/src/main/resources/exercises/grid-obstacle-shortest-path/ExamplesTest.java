package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void oneRemovalFindsTheShortRoute() {
        assertThat(Solution.shortestPath(new int[][]{{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}}, 1)).isEqualTo(6);
    }

    @Test
    void notEnoughRemovalsReturnsNegativeOne() {
        assertThat(Solution.shortestPath(new int[][]{{0,1,1},{1,1,1},{1,0,0}}, 1)).isEqualTo(-1);
    }
}
