package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void oneStopMustUseTheDirectLinkToTheHub() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 500}}, 0, 3, 1))
                .as("cheapestPrice from 0 to 3 with at most 1 stops")
                .isEqualTo(600);
    }

    @Test
    void twoStopsAllowTheCheapChain() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 500}}, 0, 3, 2))
                .as("cheapestPrice from 0 to 3 with at most 2 stops")
                .isEqualTo(300);
    }

    @Test
    void zeroStopsMeansADirectFlight() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 500}}, 0, 2, 0))
                .as("cheapestPrice from 0 to 2 with at most 0 stops")
                .isEqualTo(500);
    }
}
