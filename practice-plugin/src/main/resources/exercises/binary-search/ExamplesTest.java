package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAValueInTheMiddle() {
        int[] sorted = {-4, -1, 0, 6, 9};
        assertThat(sorted[Solution.search(sorted, 6)]).isEqualTo(6);
    }

    @Test
    void acceptsAnyMatchingDuplicateIndex() {
        int[] sorted = {1, 2, 2, 2, 8};
        assertThat(Solution.search(sorted, 2)).isIn(1, 2, 3);
    }

    @Test
    void reportsAnAbsentValue() {
        assertThat(Solution.search(new int[]{1, 4, 7}, 5)).isEqualTo(-1);
    }
}
