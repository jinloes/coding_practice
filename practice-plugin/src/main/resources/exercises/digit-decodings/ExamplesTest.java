package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void twoWays() {
        assertCount("12", 2);
    }

    @Test
    void threeWays() {
        assertCount("226", 3);
    }

    @Test
    void leadingZeroHasNoDecoding() {
        assertCount("06", 0);
    }

    private static void assertCount(String digits, int expected) {
        assertThat(Solution.count(digits)).as("count(\"%s\")", digits).isEqualTo(expected);
    }
}
