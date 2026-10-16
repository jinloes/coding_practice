package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsIntoThreePieces() {
        check("abcabdxyx", 5, 1, 3);
    }

    @Test
    void keepsOneLetterTogether() {
        check("zzzz", 4);
    }

    @Test
    void splitsAllDistinctLetters() {
        check("qrst", 1, 1, 1, 1);
    }

    private static void check(String text, Integer... expected) {
        assertThat(Solution.partitionSizes(text)).as("partitionSizes(\"%s\")".formatted(text)).containsExactly(expected);
    }
}
