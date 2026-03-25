package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TwoSumTest {

    @Test
    void emptyArray() {
        assertThat(TwoSum.find2Sum(new int[]{}, 3)).isEqualTo(Set.of());
    }

    @Test
    void simpleArray() {
        assertThat(TwoSum.find2Sum(new int[]{1, 2}, 3)).isEqualTo(Set.of(new Pair(1, 2)));
    }

    @Test
    void noMatchingPair() {
        assertThat(TwoSum.find2Sum(new int[]{3, 1, 2}, 6)).isEqualTo(Set.of());
    }

    @Test
    void arrayWithDuplicates() {
        assertThat(TwoSum.find2Sum(new int[]{6, 1, 4, 3, 1, 7, 3}, 6)).isEqualTo(Set.of(new Pair(3, 3)));
    }

    @Test
    void multiplePairs() {
        assertThat(TwoSum.find2Sum(new int[]{3, 1, 2, 4, 5, 0}, 5))
            .isEqualTo(Set.of(new Pair(2, 3), new Pair(1, 4), new Pair(0, 5)));
    }
}