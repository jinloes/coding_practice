package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void oneAlternativeForOnePair() {
        check(1, "()");
    }

    @Test
    void twoAlternativesForTwoPairs() {
        check(2, "(())", "()()");
    }

    @Test
    void fiveAlternativesForThreePairs() {
        check(3, "((()))", "(()())", "(())()", "()(())", "()()()");
    }

    private static void check(int pairs, String... expected) {
        String call = "generate(%d)".formatted(pairs);
        List<String> actual = Solution.generate(pairs);
        assertThat(actual).as(call).isNotNull();
        assertThat(actual).as(call + " must not repeat a string").doesNotHaveDuplicates();
        assertThat(actual).as(call + " in any order").containsExactlyInAnyOrder(expected);
    }
}
