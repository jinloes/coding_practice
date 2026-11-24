package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void evaluatesForwardReverseSelfAndUnknownQueries() {
        double[] actual = solution.evaluateRates(
                new String[][]{{"USD", "EUR"}, {"EUR", "GBP"}},
                new double[]{0.9, 0.8},
                new String[][]{{"USD", "GBP"}, {"GBP", "USD"}, {"USD", "USD"}, {"USD", "JPY"}});
        assertThat(actual).hasSize(4);
        assertThat(actual[0]).isCloseTo(0.72, within(1e-5));
        assertThat(actual[1]).isCloseTo(1.0 / 0.72, within(1e-5));
        assertThat(actual[2]).isCloseTo(1.0, within(1e-5));
        assertThat(actual[3]).isCloseTo(-1.0, within(1e-5));
    }

    @Test
    void returnsMinusOneAcrossDisconnectedComponents() {
        double[] actual = solution.evaluateRates(
                new String[][]{{"USD", "CAD"}, {"EUR", "GBP"}},
                new double[]{1.35, 0.8},
                new String[][]{{"USD", "GBP"}});
        assertThat(actual).containsExactly(-1.0);
    }

    @Test
    void returnsMinusOneForUnknownSelfQuery() {
        double[] actual = solution.evaluateRates(
                new String[][]{{"USD", "EUR"}},
                new double[]{0.9},
                new String[][]{{"BTC", "BTC"}});
        assertThat(actual).containsExactly(-1.0);
    }
}
