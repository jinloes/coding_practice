package com.jinloes.arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongestNonDecreasingSequenceTest {

    @Test
    void longestIncreasingSequence_simpleArray() {
        assertThat(LongestNonDecreasingSequence.findLongestIncreasingSequence(new int[]{5, 2})).isEqualTo(1);
    }

    @Test
    void longestIncreasingSequence_complexArray() {
        assertThat(LongestNonDecreasingSequence.findLongestIncreasingSequence(new int[]{1, 3, 4, 1})).isEqualTo(3);
    }

    @Test
    void longestIncreasingSequence_moreComplexArray() {
        assertThat(LongestNonDecreasingSequence.findLongestIncreasingSequence(new int[]{7, 2, 3, 1, 5, 8, 9, 6})).isEqualTo(5);
    }

    @Test
    void longestNonDecreasingSequence_emptyArray() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(new int[]{})).isEqualTo(0);
    }

    @Test
    void longestNonDecreasingSequence_singleElement() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(new int[]{1})).isEqualTo(1);
    }

    @Test
    void longestNonDecreasingSequence_ascendingArray() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(new int[]{1, 2, 3})).isEqualTo(3);
    }

    @Test
    void longestNonDecreasingSequence_complexArray() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(new int[]{5, 3, 4, 8, 6, 7})).isEqualTo(3);
    }

    @Test
    void longestNonDecreasingSequence_longerComplexArray() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(
            new int[]{5, 6, 7, 8, 3, 4, 8, 9, 10, 11, 12, 6, 7, 8, 9})).isEqualTo(7);
    }

    @Test
    void longestNonDecreasingSequence_withRepeats() {
        assertThat(LongestNonDecreasingSequence.findLongestNonDecreasingSequence(
            new int[]{1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 1, 2, 3, 4, 5, 1, 2, 3})).isEqualTo(10);
    }
}
