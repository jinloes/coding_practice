package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void combinesTwoDigits() {
        assertThat(Solution.combinations("23"))
                .containsExactlyInAnyOrder("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf");
    }

    @Test
    void usesFourLettersForSeven() {
        assertThat(Solution.combinations("7"))
                .containsExactlyInAnyOrder("p", "q", "r", "s");
    }

    @Test
    void emptyDigitsGiveNoStrings() {
        assertThat(Solution.combinations("")).isEmpty();
    }
}
