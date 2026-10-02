package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleCell() {
        assertCount(1, 1, 1);
    }

    @Test
    void singleRow() {
        assertCount(1, 5, 1);
    }

    @Test
    void singleColumn() {
        assertCount(5, 1, 1);
    }

    @Test
    void twoByTwo() {
        assertCount(2, 2, 2);
    }

    @Test
    void threeByThree() {
        assertCount(3, 3, 6);
    }

    @Test
    void tallNarrowGrid() {
        assertCount(10000, 2, 10000);
    }

    @Test
    void wideNarrowGrid() {
        assertCount(2, 10000, 10000);
    }

    @Test
    void longSingleRow() {
        assertCount(1, 10000, 1);
    }

    @Test
    void longSingleColumn() {
        assertCount(10000, 1, 1);
    }

    @Test
    void rectangularGrid() {
        assertCount(4, 9, 165);
    }

    @Test
    void countFirstExceedsIntRange() {
        assertCount(18, 18, 333606206);
    }

    @Test
    void largeSquareGridWrapsModulo() {
        assertCount(300, 300, 764315181);
    }

    private static void assertCount(int rows, int cols, int expected) {
        assertThat(Solution.count(rows, cols)).as("count(%d, %d)", rows, cols).isEqualTo(expected);
    }
}
