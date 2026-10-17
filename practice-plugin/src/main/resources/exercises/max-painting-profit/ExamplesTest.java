package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void takesBothWhenOnlyTwoHousesExist() { assertThat(Solution.maxProfit(new int[]{100, 50})).isEqualTo(150); }
    @Test
    void skipsWeakestOfThree() { assertThat(Solution.maxProfit(new int[]{100, 70, 60})).isEqualTo(170); }
    @Test
    void combinesSeparateRuns() { assertThat(Solution.maxProfit(new int[]{40, 100, 50, 20, 100})).isEqualTo(260); }
}

