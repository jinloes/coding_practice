package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsIntoSmallerPalindromes() {
        assertThat(Solution.partitions("aab")).map(List::toString)
                .containsExactlyInAnyOrder("[a, a, b]", "[aa, b]");
    }

    @Test
    void singleLettersAlwaysWork() {
        assertThat(Solution.partitions("abc")).map(List::toString)
                .containsExactlyInAnyOrder("[a, b, c]");
    }

    @Test
    void findsNestedPalindromes() {
        assertThat(Solution.partitions("racecar")).map(List::toString)
                .containsExactlyInAnyOrder("[r, a, c, e, c, a, r]", "[r, a, cec, a, r]", "[r, aceca, r]", "[racecar]");
    }
}
