package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsASubsequenceInTheSecondString() {
        assertThat(Solution.lcsLength("abcde", "ace")).as("lcsLength(abcde, ace)").isEqualTo(3);
    }

    @Test
    void reportsZeroForDisjointStrings() {
        assertThat(Solution.lcsLength("abc", "xyz")).as("lcsLength(abc, xyz)").isEqualTo(0);
    }

    @Test
    void skipsInterleavedCharacters() {
        assertThat(Solution.lcsLength("axbycz", "abc")).as("lcsLength(axbycz, abc)").isEqualTo(3);
    }
}
