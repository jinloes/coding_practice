package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndSingleCharacterStringsPass() { assertThat(Solution.canPermutePalindrome("")).isTrue(); assertThat(Solution.canPermutePalindrome("z")).isTrue(); }
    @Test
    void evenCountsAllAroundPass() { assertThat(Solution.canPermutePalindrome("aabbccdd")).isTrue(); }
    @Test
    void caseIsSignificant() { assertThat(Solution.canPermutePalindrome("Aa")).isFalse(); }
    @Test
    void punctuationIsCounted() { assertThat(Solution.canPermutePalindrome("!!??x")).isTrue(); assertThat(Solution.canPermutePalindrome("!!??xy")).isFalse(); }
    @Test
    void handlesLargeInput() { String value = "abccba".repeat(10000) + "z"; assertThat(Solution.canPermutePalindrome(value)).isTrue(); assertThat(Solution.canPermutePalindrome(value + "y")).isFalse(); }
}
