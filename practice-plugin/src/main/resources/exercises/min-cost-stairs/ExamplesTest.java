package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void landingOnTheSecondStepIsEnough() {
        assertThat(Solution.minCostToTop(new int[]{10, 15, 20}))
                .as("minCostToTop for 3 steps")
                .isEqualTo(15);
    }

    @Test
    void skippingTheExpensiveSteps() {
        assertThat(Solution.minCostToTop(new int[]{4, 9, 2, 7, 1, 8}))
                .as("minCostToTop for 6 steps")
                .isEqualTo(7);
    }

    @Test
    void emptyStaircaseCostsNothing() {
        assertThat(Solution.minCostToTop(new int[0]))
                .as("minCostToTop for 0 steps")
                .isEqualTo(0);
    }
}
