package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsOddLengthPalindrome() { assertThat(Solution.isPalindrome(12321)).isTrue(); }
    @Test
    void rejectsNegativeNumber() { assertThat(Solution.isPalindrome(-121)).isFalse(); }
    @Test
    void rejectsNonPalindromeEndingInZero() { assertThat(Solution.isPalindrome(12310)).isFalse(); }
}
