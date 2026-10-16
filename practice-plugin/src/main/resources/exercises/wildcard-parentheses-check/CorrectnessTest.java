package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void onlyStars() {
        check("***", true);
    }

    @Test
    void starAsEmpty() {
        check("(*", true);
    }

    @Test
    void notEnoughStarsToClose() {
        check("((*", false);
    }

    @Test
    void enoughStarsToClose() {
        check("((**", true);
    }

    @Test
    void closeBeforeOpenFails() {
        check(")(", false);
    }

    @Test
    void starBeforeCloseCanOpen() {
        check("*)", true);
    }

    @Test
    void starsCannotRescueLeadingClose() {
        check(")*", false);
    }

    @Test
    void starsAfterOpensBeforeClose() {
        check("(*()", true);
    }

    @Test
    void starsLeftOfOpenCannotClose() {
        check("**((", false);
    }

    @Test
    void longBalanced() {
        check("(".repeat(1000) + "*".repeat(1000), true);
    }

    @Test
    void longUnbalanced() {
        check("(".repeat(1000) + "*".repeat(999), false);
    }

    private static void check(String pattern, boolean expected) {
        assertThat(Solution.isBalanced(pattern)).as("isBalanced(\"%s\")".formatted(pattern)).isEqualTo(expected);
    }
}
