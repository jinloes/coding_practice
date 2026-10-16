package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void starAbsorbsRepeatedLetters() {
        check("moon", "mo*n", true);
    }

    @Test
    void patternEndingEarlyDoesNotMatch() {
        check("moon", "mo.", false);
    }

    @Test
    void starredPairsMayMatchNothing() {
        check("", "a*b*", true);
    }

    private static void check(String text, String pattern, boolean expected) {
        String call = "matches(\"%s\", \"%s\")".formatted(text, pattern);
        assertThat(Solution.matches(text, pattern)).as(call).isEqualTo(expected);
    }
}
