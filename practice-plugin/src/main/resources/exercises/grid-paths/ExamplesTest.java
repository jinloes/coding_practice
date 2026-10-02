package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void sevenColumns() {
        assertCount(3, 7, 28);
    }

    @Test
    void twoColumns() {
        assertCount(3, 2, 3);
    }

    @Test
    void singleCell() {
        assertCount(1, 1, 1);
    }

    private static void assertCount(int rows, int cols, int expected) {
        assertThat(Solution.count(rows, cols)).as("count(%d, %d)", rows, cols).isEqualTo(expected);
    }
}
