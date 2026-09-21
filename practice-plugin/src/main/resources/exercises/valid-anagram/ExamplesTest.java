package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsAReorderingOfTheSameLetters() {
        assertThat(Solution.isAnagram("listen", "silent"))
                .as("isAnagram(\"listen\", \"silent\") must accept a reordering of the same letters")
                .isTrue();
    }

    @Test
    void rejectsDifferentLetters() {
        assertThat(Solution.isAnagram("rat", "car"))
                .as("isAnagram(\"rat\", \"car\") must reject words built from different letters")
                .isFalse();
    }

    @Test
    void rejectsTheSameLettersInDifferentAmounts() {
        assertThat(Solution.isAnagram("aacc", "ccac"))
                .as("isAnagram(\"aacc\", \"ccac\") must reject counts that do not match")
                .isFalse();
    }
}
