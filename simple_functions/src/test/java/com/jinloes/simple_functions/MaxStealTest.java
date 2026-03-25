package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxStealTest {

    @Test
    void oneHouse() {
        assertThat(MaxSteal.steal(new int[]{3})).isEqualTo(3);
    }

    @Test
    void twoHouses() {
        assertThat(MaxSteal.steal(new int[]{3, 5})).isEqualTo(5);
    }

    @Test
    void fourHouses() {
        assertThat(MaxSteal.steal(new int[]{6, 1, 2, 7})).isEqualTo(13);
    }
}