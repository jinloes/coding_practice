package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void buildsTableForFive() {
        check(5, 0, 1, 1, 2, 1, 2);
    }

    @Test
    void buildsTableForZero() {
        check(0, 0);
    }

    @Test
    void buildsTableForTwo() {
        check(2, 0, 1, 1);
    }

    private static void check(int n, int... expected) {
        assertThat(Solution.countBits(n)).as("countBits(%d)", n).containsExactly(expected);
    }
}
