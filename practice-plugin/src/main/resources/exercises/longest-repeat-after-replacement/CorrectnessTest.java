package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsZeroForEmptyText() {
        assertThat(Solution.longestRepeat("", 3)).isZero();
    }

    @Test
    void returnsTheWholeTextWhenKCoversIt() {
        assertThat(Solution.longestRepeat("ABCDE", 5)).isEqualTo(5);
    }

    @Test
    void returnsTheWholeTextWhenKIsLarger() {
        assertThat(Solution.longestRepeat("ABC", 100)).isEqualTo(3);
    }

    @Test
    void doesNotJustAddKToTheMostFrequentLetter() {
        assertThat(Solution.longestRepeat("ABCDEFA", 1)).isEqualTo(2);
    }

    @Test
    void findsAnExistingRunWithNoReplacements() {
        assertThat(Solution.longestRepeat("AABBBA", 0)).isEqualTo(3);
    }

    @Test
    void handlesASingleLetterText() {
        assertThat(Solution.longestRepeat("AAAA", 0)).isEqualTo(4);
    }

    @Test
    void canChooseALetterThatIsNotTheFirst() {
        assertThat(Solution.longestRepeat("ABBBAC", 1)).isEqualTo(4);
    }

    @Test
    void handlesAlternatingLettersWithLargeK() {
        assertThat(Solution.longestRepeat("ABABABAB", 3)).isEqualTo(7);
    }

    @Test
    void handlesAnEarlyBestWindowFollowedByNoise() {
        assertThat(Solution.longestRepeat("AAAAXBCDEF", 1)).isEqualTo(5);
    }

    @Test
    void handlesALongText() {
        assertThat(Solution.longestRepeat("ABC".repeat(10_000), 4)).isEqualTo(7);
    }

    @Test
    void handlesAVeryLargeK() {
        assertThat(Solution.longestRepeat("ZYX", Integer.MAX_VALUE)).isEqualTo(3);
    }
}
