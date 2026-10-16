package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void sameCityCostsNothing() {
        assertThat(Solution.cheapestPrice(3, new int[][]{{0, 1, 5}}, 1, 1, 0))
                .as("cheapestPrice from 1 to 1 with at most 0 stops")
                .isEqualTo(0);
    }

    @Test
    void tooFewStopsEvenThoughAPathExists() {
        assertThat(Solution.cheapestPrice(5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}}, 0, 4, 2))
                .as("cheapestPrice from 0 to 4 with at most 2 stops")
                .isEqualTo(-1);
    }

    @Test
    void exactlyEnoughStops() {
        assertThat(Solution.cheapestPrice(5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}}, 0, 4, 3))
                .as("cheapestPrice from 0 to 4 with at most 3 stops")
                .isEqualTo(4);
    }

    @Test
    void cheapestRouteMayNeedMoreStopsThanTheDirectOne() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 3, 900}, {0, 1, 100}, {1, 2, 100}, {2, 3, 100}}, 0, 3, 2))
                .as("cheapestPrice from 0 to 3 with at most 2 stops")
                .isEqualTo(300);
    }

    @Test
    void directFlightWinsWhenStopsAreLimited() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 3, 900}, {0, 1, 100}, {1, 2, 100}, {2, 3, 100}}, 0, 3, 1))
                .as("cheapestPrice from 0 to 3 with at most 1 stops")
                .isEqualTo(900);
    }

    @Test
    void parallelFlightsUseTheCheapest() {
        assertThat(Solution.cheapestPrice(2, new int[][]{{0, 1, 80}, {0, 1, 30}, {0, 1, 55}}, 0, 1, 0))
                .as("cheapestPrice from 0 to 1 with at most 0 stops")
                .isEqualTo(30);
    }

    @Test
    void unreachableCityGivesMinusOne() {
        assertThat(Solution.cheapestPrice(4, new int[][]{{0, 1, 5}, {2, 3, 5}}, 0, 3, 3))
                .as("cheapestPrice from 0 to 3 with at most 3 stops")
                .isEqualTo(-1);
    }

    @Test
    void cyclesDoNotHelp() {
        assertThat(Solution.cheapestPrice(3, new int[][]{{0, 1, 10}, {1, 0, 1}, {1, 2, 10}}, 0, 2, 5))
                .as("cheapestPrice from 0 to 2 with at most 5 stops")
                .isEqualTo(20);
    }

    @Test
    void limitLargerThanCityCount() {
        assertThat(Solution.cheapestPrice(3, new int[][]{{0, 1, 4}, {1, 2, 4}}, 0, 2, 50))
                .as("cheapestPrice from 0 to 2 with at most 50 stops")
                .isEqualTo(8);
    }

    @Test
    void flightsListedInReverseOrder() {
        assertThat(Solution.cheapestPrice(5, new int[][]{{3, 4, 1}, {2, 3, 1}, {1, 2, 1}, {0, 1, 1}}, 0, 4, 3))
                .as("cheapestPrice from 0 to 4 with at most 3 stops")
                .isEqualTo(4);
    }

    @Test
    void reverseOrderStillRespectsTheLimit() {
        assertThat(Solution.cheapestPrice(5, new int[][]{{3, 4, 1}, {2, 3, 1}, {1, 2, 1}, {0, 1, 1}}, 0, 4, 1))
                .as("cheapestPrice from 0 to 4 with at most 1 stops")
                .isEqualTo(-1);
    }

    @Test
    void longChainWithLimitedStops() {
        int cities = 3_000;
        int[][] flights = new int[cities - 1][];
        for (int i = 0; i < cities - 1; i++) {
            flights[i] = new int[]{i, i + 1, 2};
        }
        assertThat(Solution.cheapestPrice(cities, flights, 0, cities - 1, cities - 2)).isEqualTo(2 * (cities - 1));
        assertThat(Solution.cheapestPrice(cities, flights, 0, cities - 1, cities - 3)).isEqualTo(-1);
    }

    @Test
    void doesNotChangeTheFlights() {
        int[][] flights = {{0, 1, 7}, {1, 2, 8}};
        Solution.cheapestPrice(3, flights, 0, 2, 1);
        assertThat(flights).isDeepEqualTo(new int[][]{{0, 1, 7}, {1, 2, 8}});
    }
}
