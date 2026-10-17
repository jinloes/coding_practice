package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsRearrangeableOddLengthString() { assertThat(Solution.canPermutePalindrome("carrace")).isTrue(); }
    @Test
    void rejectsTooManyOddCounts() { assertThat(Solution.canPermutePalindrome("daily")).isFalse(); }
    @Test
    void countsSpacesAsCharacters() { assertThat(Solution.canPermutePalindrome("aabb c")).isFalse(); }
}
