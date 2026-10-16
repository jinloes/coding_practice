package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reachesOne() {
        check(19, true);
    }

    @Test
    void fallsIntoCycle() {
        check(4, false);
    }

    @Test
    void oneIsHappy() {
        check(1, true);
    }

    private static void check(int n, boolean expected) {
        assertThat(Solution.isHappy(n)).as("isHappy(%d)".formatted(n)).isEqualTo(expected);
    }
}
