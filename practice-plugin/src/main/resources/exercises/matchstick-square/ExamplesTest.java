package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void formsSquareFromFiveSticks() {
        assertThat(Solution.canFormSquare(new int[]{1, 1, 2, 2, 2})).isTrue();
    }
    @Test
    void rejectsUnevenTotal() {
        assertThat(Solution.canFormSquare(new int[]{3, 3, 3, 3, 4})).isFalse();
    }
    @Test
    void rejectsZeroAreaSquare() {
        assertThat(Solution.canFormSquare(new int[]{0, 0, 0, 0})).isFalse();
    }
}
