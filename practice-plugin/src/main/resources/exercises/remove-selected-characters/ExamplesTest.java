package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesVowelsFromSentence() {
        assertThat(Solution.removeSelected("Battle of the Vowels: Hawaii vs. Grozny", "aeiou"))
                .isEqualTo("Bttl f th Vwls: Hw vs. Grzny");
    }

    @Test
    void removesRepeatedLettersFromSet() {
        assertThat(Solution.removeSelected("abracadabra", "ab")).isEqualTo("rcdr");
    }

    @Test
    void keepsTextWhenSetDoesNotMatch() {
        assertThat(Solution.removeSelected("keep", "xyz")).isEqualTo("keep");
    }
}
