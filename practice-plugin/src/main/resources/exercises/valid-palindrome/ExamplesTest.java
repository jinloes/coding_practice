package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void ignoresCaseSpacesAndPunctuation() {
        assertThat(Solution.isPalindrome("Was it a car or a cat I saw?"))
                .as("isPalindrome(\"Was it a car or a cat I saw?\") must ignore case, spaces, and punctuation")
                .isTrue();
    }

    @Test
    void rejectsTextThatDiffersWhenReversed() {
        assertThat(Solution.isPalindrome("race a car"))
                .as("isPalindrome(\"race a car\") must reject letters that do not mirror")
                .isFalse();
    }

    @Test
    void acceptsTextWithNoLettersOrDigits() {
        assertThat(Solution.isPalindrome(".,!"))
                .as("isPalindrome(\".,!\") must accept text with nothing to compare")
                .isTrue();
    }
}
