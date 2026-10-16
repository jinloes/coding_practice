package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reversesAndDropsTrailingZeros() {
        assertThat(Solution.reverseDigits(1203)).as("reverseDigits(1203)").isEqualTo(3021);
    }

    @Test
    void keepsTheSignOfNegativeNumbers() {
        assertThat(Solution.reverseDigits(-450)).as("reverseDigits(-450)").isEqualTo(-54);
    }

    @Test
    void overflowReturnsZero() {
        assertThat(Solution.reverseDigits(1563847412)).as("reverseDigits(1563847412)").isZero();
    }
}
