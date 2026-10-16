package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void idlesBetweenRepeatedPairs() {
        assertThat(Solution.minSlots("AAABBB", 2)).as("minSlots of AAABBB with cooldown 2").isEqualTo(8);
    }

    @Test
    void noIdleWhenTasksAreVaried() {
        assertThat(Solution.minSlots("ABCD", 3)).as("minSlots of ABCD with cooldown 3").isEqualTo(4);
    }

    @Test
    void lonelyTaskForcesIdleGaps() {
        assertThat(Solution.minSlots("AAAB", 2)).as("minSlots of AAAB with cooldown 2").isEqualTo(7);
    }

}
