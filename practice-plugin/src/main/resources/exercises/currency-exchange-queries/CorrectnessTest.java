package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void handlesCyclesWithoutInfiniteLoop() {
        double[] actual = solution.evaluateRates(
                new String[][]{{"A", "B"}, {"B", "C"}, {"C", "A"}, {"C", "D"}},
                new double[]{2.0, 3.0, 1.0 / 6.0, 4.0},
                new String[][]{{"A", "D"}, {"D", "B"}, {"A", "Z"}});
        assertThat(actual[0]).isCloseTo(24.0, within(1e-5));
        assertThat(actual[1]).isCloseTo(1.0 / 12.0, within(1e-5));
        assertThat(actual[2]).isCloseTo(-1.0, within(1e-5));
    }

    @Test
    void handlesEmptyPairsAndEmptyQueries() {
        assertThat(solution.evaluateRates(new String[][]{}, new double[]{}, new String[][]{{"A", "B"}}))
                .containsExactly(-1.0);
        assertThat(solution.evaluateRates(new String[][]{{"A", "B"}}, new double[]{2.0}, new String[][]{}))
                .isEmpty();
    }

    @Test
    void chainsMultipleReverseEdges() {
        double[] actual = solution.evaluateRates(
                new String[][]{{"A", "B"}, {"B", "C"}, {"C", "D"}},
                new double[]{2.0, 5.0, 4.0},
                new String[][]{{"D", "A"}});
        assertThat(actual[0]).isCloseTo(0.025, within(1e-5));
    }
}
