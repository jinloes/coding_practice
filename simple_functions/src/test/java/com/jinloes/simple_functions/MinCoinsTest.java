package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinCoinsTest {

    @Test
    void findMinCoins() {
        assertThat(MinCoins.findMinCoins(0, new int[]{1, 3, 5})).isEqualTo(0);
        assertThat(MinCoins.findMinCoins(1, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoins(3, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoins(5, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoins(4, new int[]{1, 3, 5})).isEqualTo(2);
        assertThat(MinCoins.findMinCoins(11, new int[]{1, 3, 5})).isEqualTo(3);
        assertThat(MinCoins.findMinCoins(15, new int[]{1, 3, 5})).isEqualTo(3);
        assertThat(MinCoins.findMinCoins(21, new int[]{1, 3, 5})).isEqualTo(5);
        assertThat(MinCoins.findMinCoins(16, new int[]{1, 2, 4, 6})).isEqualTo(3);
        assertThat(MinCoins.findMinCoins(35, new int[]{1, 2, 4, 6})).isEqualTo(7);
    }

    @Test
    void findMinCoinsAlternative() {
        assertThat(MinCoins.findMinCoinsAlternative(0, new int[]{1, 3, 5})).isEqualTo(0);
        assertThat(MinCoins.findMinCoinsAlternative(1, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoinsAlternative(3, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoinsAlternative(5, new int[]{1, 3, 5})).isEqualTo(1);
        assertThat(MinCoins.findMinCoinsAlternative(4, new int[]{1, 3, 5})).isEqualTo(2);
        assertThat(MinCoins.findMinCoinsAlternative(11, new int[]{1, 3, 5})).isEqualTo(3);
        assertThat(MinCoins.findMinCoinsAlternative(15, new int[]{1, 3, 5})).isEqualTo(3);
        assertThat(MinCoins.findMinCoinsAlternative(21, new int[]{1, 3, 5})).isEqualTo(5);
        assertThat(MinCoins.findMinCoinsAlternative(16, new int[]{1, 2, 4, 6})).isEqualTo(3);
        assertThat(MinCoins.findMinCoinsAlternative(35, new int[]{1, 2, 4, 6})).isEqualTo(7);
        assertThat(MinCoins.findMinCoinsAlternative(1, new int[]{2, 3})).isEqualTo(0);
    }
}