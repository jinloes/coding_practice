package com.jinloes.dynamic_programming;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinsTest {
    private Coins coins;

    @BeforeEach
    public void setUp() throws Exception {
        coins = new Coins();
    }

    @Test
    public void numWays() {
        assertThat(coins.numWays(1)).isEqualTo(1);
        assertThat(coins.numWays(5)).isEqualTo(2);
        assertThat(coins.numWays(10)).isEqualTo(9);
        assertThat(coins.numWays(25)).isEqualTo(916);
    }

    @Test
    @Timeout(value = 50, unit = java.util.concurrent.TimeUnit.MILLISECONDS)
    public void numWaysLarge() {
        assertThat(coins.numWays(45)).isEqualTo(418060);
    }
}