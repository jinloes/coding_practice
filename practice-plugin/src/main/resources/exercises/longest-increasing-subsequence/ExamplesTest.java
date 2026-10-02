package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsASubsequenceThatSkipsElements() {
        assertThat(Solution.lengthOfLis(new int[]{4, 10, 5, 8, 6, 9})).isEqualTo(4);
    }

    @Test
    void doesNotCountEqualValuesTwice() {
        assertThat(Solution.lengthOfLis(new int[]{3, 3, 3})).isEqualTo(1);
    }

    @Test
    void handlesAStrictlyDecreasingArray() {
        assertThat(Solution.lengthOfLis(new int[]{9, 7, 5, 1})).isEqualTo(1);
    }
}
