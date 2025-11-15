package com.jinloes.graphs;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneLetterCombinationsTest {

    @Test
    void nullAndEmptyReturnEmptyList() {
        List<String> nullResult = PhoneLetterCombinations.letterCombinations(null);
        List<String> emptyResult = PhoneLetterCombinations.letterCombinations("");
        assertThat(nullResult).isEmpty();
        assertThat(emptyResult).isEmpty();
    }

    @Test
    void singleDigitProducesExpectedLetters() {
        List<String> combos = PhoneLetterCombinations.letterCombinations("2");
        assertThat(combos)
                .containsExactly("a", "b", "c");
    }

    @Test
    void twoDigitsProduceCartesianProductInExpectedOrder() {
        List<String> combos = PhoneLetterCombinations.letterCombinations("23");
        assertThat(combos)
                .containsExactly("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf");
    }

    @Test
    void digitsWithZeroOrOneAreSkipped() {
        // "203" should behave like "23" because 0 maps to empty and is skipped
        List<String> combos = PhoneLetterCombinations.letterCombinations("203");

        assertThat(combos)
                .hasSize(9);
        assertThat(combos)
                .containsExactly("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf");
    }

    @Test
    void longRepeatedDigitProducesExpectedCountAndBounds() {
        // seven 2's -> 3^7 combinations
        String digits = "2222222";
        List<String> combos = PhoneLetterCombinations.letterCombinations(digits);
        assertThat(combos)
                .hasSize((int) Math.pow(3, digits.length()));
        // first should be all 'a's, last all 'c's for mapping "abc"
        String first = "a".repeat(digits.length());
        String last = "c".repeat(digits.length());
        assertThat(combos.get(0))
                .isEqualTo(first);
        assertThat(combos.get(combos.size() - 1))
                .isEqualTo(last);
    }
}