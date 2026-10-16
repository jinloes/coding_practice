package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsPositionChoices() {
        assertThat(Solution.countSubsequences("bagbag", "bag")).as("countSubsequences(\"bagbag\", \"bag\")").isEqualTo(4);
    }

    @Test
    void rejectsWrongOrder() {
        assertThat(Solution.countSubsequences("xyz", "xzy")).as("countSubsequences(\"xyz\", \"xzy\")").isEqualTo(0);
    }

    @Test
    void countsPairsOfRepeatedLetters() {
        assertThat(Solution.countSubsequences("aaaa", "aa")).as("countSubsequences(\"aaaa\", \"aa\")").isEqualTo(6);
    }
}
