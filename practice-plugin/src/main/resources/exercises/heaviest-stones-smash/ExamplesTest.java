package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void leavesOneLightStone() {
        assertThat(Solution.smash(new int[]{8, 3, 5, 1})).as("smash of [8, 3, 5, 1]").isEqualTo(1);
    }

    @Test
    void equalPairVanishes() {
        assertThat(Solution.smash(new int[]{6, 6})).as("smash of [6, 6]").isEqualTo(0);
    }

    @Test
    void singleStoneRemains() {
        assertThat(Solution.smash(new int[]{20})).as("smash of [20]").isEqualTo(20);
    }

}
