package com.jinloes.simple_functions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MinCostCookingTimerTest {
    private MinCostCookingTimer minCostCookingTimer = new MinCostCookingTimer();

    @ParameterizedTest
    @CsvSource({"1,2,1,600,6", "0,1,2,76,6", "1,1,1,1,1"})
    public void testMinCostSetTime(int startAt, int moveCost, int pushCost, int targetSeconds, int expected) {
        assertThat(minCostCookingTimer.minCostSetTime(startAt, moveCost, pushCost, targetSeconds))
                .isEqualTo(expected);
    }
}