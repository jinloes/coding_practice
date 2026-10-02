package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void measuresAWindowThatSlidesPastARepeat() {
        assertThat(Solution.longestUniqueLength("abacdbe")).isEqualTo(5);
    }

    @Test
    void returnsZeroForEmptyText() {
        assertThat(Solution.longestUniqueLength("")).isZero();
    }

    @Test
    void returnsOneWhenEverythingRepeats() {
        assertThat(Solution.longestUniqueLength("zzzz")).isEqualTo(1);
    }
}
