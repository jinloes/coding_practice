package com.jinloes.simple_functions.dynamic_programming;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinsTest {
    private Coins coins;

    @Before
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

    @Test(timeout = 50)
    public void numWaysLarge() {
        assertThat(coins.numWays(45)).isEqualTo(418060);
    }
}