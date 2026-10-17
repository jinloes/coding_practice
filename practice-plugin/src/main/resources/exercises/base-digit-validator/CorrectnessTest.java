package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void rejectsNullEmptyAndPrefixOnly() {
        assertThat(Solution.isValidForBase(null, 10)).isFalse();
        assertThat(Solution.isValidForBase("", 10)).isFalse();
        assertThat(Solution.isValidForBase("0x", 16)).isFalse();
    }
    @Test
    void enforcesBaseRange() {
        assertThat(Solution.isValidForBase("1", 1)).isFalse();
        assertThat(Solution.isValidForBase("1", 37)).isFalse();
    }
    @Test
    void acceptsHighestDigitForLargeBases() {
        assertThat(Solution.isValidForBase("zZ9", 36)).isTrue();
        assertThat(Solution.isValidForBase("fF", 16)).isTrue();
    }
    @Test
    void rejectsLettersTooLargeForBase() {
        assertThat(Solution.isValidForBase("ffff", 12)).isFalse();
        assertThat(Solution.isValidForBase("g", 16)).isFalse();
    }
    @Test
    void hexadecimalPrefixOnlyBelongsToBaseSixteen() {
        assertThat(Solution.isValidForBase("0x10", 16)).isTrue();
        assertThat(Solution.isValidForBase("0x10", 10)).isFalse();
    }
    @Test
    void rejectsSignsWhitespaceAndSeparators() {
        assertThat(Solution.isValidForBase("+101", 2)).isFalse();
        assertThat(Solution.isValidForBase("10 1", 2)).isFalse();
        assertThat(Solution.isValidForBase("10_1", 2)).isFalse();
    }
}
