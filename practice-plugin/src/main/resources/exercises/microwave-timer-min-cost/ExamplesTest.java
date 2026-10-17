package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void choosesTenMinutesAsFourDigits() { assertThat(Solution.minCost(1,2,1,600)).isEqualTo(6); }
    @Test
    void choosesSecondsOverMinuteRepresentationWhenCheaper() { assertThat(Solution.minCost(0,1,2,76)).isEqualTo(6); }
    @Test
    void reusesStartingDigitForFreeMovement() { assertThat(Solution.minCost(9,100,1,9)).isEqualTo(1); }
}
