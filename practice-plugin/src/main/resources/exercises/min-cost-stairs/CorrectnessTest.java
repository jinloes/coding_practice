package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void oneStepIsFree() {
        assertThat(Solution.minCostToTop(new int[]{5}))
                .as("minCostToTop for 1 steps")
                .isEqualTo(0);
    }

    @Test
    void twoStepsPayTheCheaper() {
        assertThat(Solution.minCostToTop(new int[]{8, 3}))
                .as("minCostToTop for 2 steps")
                .isEqualTo(3);
    }

    @Test
    void equalCostsEverywhere() {
        assertThat(Solution.minCostToTop(new int[]{2, 2, 2, 2, 2, 2, 2}))
                .as("minCostToTop for 7 steps")
                .isEqualTo(6);
    }

    @Test
    void zeroCostStepsAreFree() {
        assertThat(Solution.minCostToTop(new int[]{0, 0, 0, 0}))
                .as("minCostToTop for 4 steps")
                .isEqualTo(0);
    }

    @Test
    void increasingCosts() {
        assertThat(Solution.minCostToTop(new int[]{1, 2, 3, 4, 5, 6, 7, 8}))
                .as("minCostToTop for 8 steps")
                .isEqualTo(16);
    }

    @Test
    void alternatingExpensiveSteps() {
        assertThat(Solution.minCostToTop(new int[]{1, 100, 1, 100, 1, 100, 1, 100}))
                .as("minCostToTop for 8 steps")
                .isEqualTo(4);
    }

    @Test
    void expensiveStepsInARow() {
        assertThat(Solution.minCostToTop(new int[]{1, 1, 50, 50, 1, 1}))
                .as("minCostToTop for 6 steps")
                .isEqualTo(52);
    }

    @Test
    void largeCostsStillFit() {
        assertThat(Solution.minCostToTop(new int[]{1000000, 1, 1000000, 1, 1000000, 1}))
                .as("minCostToTop for 6 steps")
                .isEqualTo(3);
    }

    @Test
    void startMustLandOnFirstOrSecondStep() {
        assertThat(Solution.minCostToTop(new int[]{100, 100, 1, 1, 1, 1}))
                .as("minCostToTop for 6 steps")
                .isEqualTo(102);
    }

    @Test
    void longStaircaseOfOnes() {
        int[] costs = new int[1_001];
        java.util.Arrays.fill(costs, 1);
        assertThat(Solution.minCostToTop(costs)).as("minCostToTop for %d steps", costs.length).isEqualTo(500);
    }

    @Test
    void doesNotChangeTheCosts() {
        int[] costs = {3, 1, 4, 1, 5};
        Solution.minCostToTop(costs);
        assertThat(costs).containsExactly(3, 1, 4, 1, 5);
    }
}
