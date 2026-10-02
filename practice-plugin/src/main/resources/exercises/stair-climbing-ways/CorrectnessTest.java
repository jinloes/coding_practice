package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void hasOneWayToClimbZeroStairs() {
        assertThat(Solution.countWays(0)).as("countWays(0)").isEqualTo(1L);
    }

    @Test
    void countsTwoAndThreeStairs() {
        assertThat(Solution.countWays(2)).as("countWays(2)").isEqualTo(2L);
        assertThat(Solution.countWays(3)).as("countWays(3)").isEqualTo(3L);
    }

    @Test
    void countsFiftyStairsBeyondIntRange() {
        assertThat(Solution.countWays(50)).as("countWays(50)").isEqualTo(20365011074L);
    }

    @Test
    void countsTheMaximumStaircaseWithoutOverflow() {
        assertThat(Solution.countWays(90)).as("countWays(90)").isEqualTo(4660046610375530309L);
    }

    @Test
    void satisfiesTheRecurrenceForEverySupportedSize() {
        for (int steps = 2; steps <= 90; steps++) {
            assertThat(Solution.countWays(steps))
                    .as("countWays(%d)", steps)
                    .isEqualTo(Solution.countWays(steps - 1) + Solution.countWays(steps - 2));
        }
    }
}
