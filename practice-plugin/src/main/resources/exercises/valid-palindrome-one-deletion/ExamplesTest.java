package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsExistingPalindrome() {
        assertThat(Solution.canBePalindrome("aba"))
                .as("canBePalindrome(\"aba\") must accept an existing palindrome")
                .isTrue();
    }

    @Test
    void acceptsSingleDeletionPalindrome() {
        assertThat(Solution.canBePalindrome("abca"))
                .as("canBePalindrome(\"abca\") must accept deleting 'b' or 'c'")
                .isTrue();
    }

    @Test
    void rejectsTwoRequiredDeletions() {
        assertThat(Solution.canBePalindrome("abc"))
                .as("canBePalindrome(\"abc\") must reject when more than one deletion is needed")
                .isFalse();
    }
}
