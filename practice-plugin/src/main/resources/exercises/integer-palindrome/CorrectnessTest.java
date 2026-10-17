package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void treatsZeroAndSingleDigitsAsPalindromes() { assertThat(Solution.isPalindrome(0)).isTrue(); assertThat(Solution.isPalindrome(7)).isTrue(); }
    @Test
    void acceptsEvenLengthPalindrome() { assertThat(Solution.isPalindrome(1221)).isTrue(); }
    @Test
    void rejectsAlmostPalindrome() { assertThat(Solution.isPalindrome(123421)).isFalse(); }
    @Test
    void handlesLargePalindrome() { assertThat(Solution.isPalindrome(2_147_447_412)).isTrue(); }
    @Test
    void handlesIntegerBoundariesWithoutOverflow() { assertThat(Solution.isPalindrome(Integer.MAX_VALUE)).isFalse(); assertThat(Solution.isPalindrome(Integer.MIN_VALUE)).isFalse(); }
}
