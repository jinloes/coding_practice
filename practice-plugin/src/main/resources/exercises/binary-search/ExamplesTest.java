package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAValueInTheMiddle() {
        int[] sorted = {-4, -1, 0, 6, 9};
        int index = Solution.search(sorted, 6);
        assertThat(index).as("search(%s, 6) must return an in-bounds index", Arrays.toString(sorted))
                .isBetween(0, sorted.length - 1);
        assertThat(sorted[index])
                .as("search(%s, 6) returned index %d", Arrays.toString(sorted), index)
                .isEqualTo(6);
    }

    @Test
    void acceptsAnyMatchingDuplicateIndex() {
        int[] sorted = {1, 2, 2, 2, 8};
        assertThat(Solution.search(sorted, 2))
                .as("search(%s, 2) must return an index holding 2", Arrays.toString(sorted))
                .isIn(1, 2, 3);
    }

    @Test
    void reportsAnAbsentValue() {
        int[] sorted = {1, 4, 7};
        assertThat(Solution.search(sorted, 5))
                .as("search(%s, 5) must return -1 because the target is absent", Arrays.toString(sorted))
                .isEqualTo(-1);
    }
}
