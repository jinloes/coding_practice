package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNoCoins() {
        assertThat(Solution.minCoins(new int[0], 5)).isEqualTo(-1);
        assertThat(Solution.minCoins(new int[0], 0)).isEqualTo(0);
    }

    @Test
    void usesASingleCoinExactly() {
        assertThat(Solution.minCoins(new int[]{9}, 9)).isEqualTo(1);
    }

    @Test
    void repeatsACoinValue() {
        assertThat(Solution.minCoins(new int[]{4}, 12)).isEqualTo(3);
        assertThat(Solution.minCoins(new int[]{4}, 10)).isEqualTo(-1);
    }

    @Test
    void ignoresDuplicateCoinValues() {
        assertThat(Solution.minCoins(new int[]{2, 2, 5, 5}, 9)).isEqualTo(3);
    }

    @Test
    void findsTheOptimumWhenGreedyIsWrong() {
        assertThat(Solution.minCoins(new int[]{1, 15, 25}, 30)).isEqualTo(2);
        assertThat(Solution.minCoins(new int[]{2, 5, 10, 1}, 27)).isEqualTo(4);
    }

    @Test
    void handlesAmountsSmallerThanEveryCoin() {
        assertThat(Solution.minCoins(new int[]{3, 7}, 2)).isEqualTo(-1);
    }

    @Test
    void handlesLargeAmounts() {
        assertThat(Solution.minCoins(new int[]{7, 13, 29}, 100000)).isEqualTo(3452);
    }

    @Test
    void doesNotMutateCoins() {
        int[] coins = {5, 1, 3};
        Solution.minCoins(coins, 11);
        assertThat(coins).containsExactly(5, 1, 3);
    }
}
