package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyScheduleTakesNoTime() {
        assertThat(Solution.minSlots("", 5)).as("minSlots of an empty task list").isEqualTo(0);
    }

    @Test
    void zeroCooldownIsJustTheTaskCount() {
        assertThat(Solution.minSlots("AAAB", 0)).as("minSlots of AAAB with cooldown 0").isEqualTo(4);
    }

    @Test
    void manyDistinctTasksFillEveryGap() {
        assertThat(Solution.minSlots("AAABBC", 1)).as("minSlots of AAABBC with cooldown 1").isEqualTo(6);
    }

    @Test
    void tiedBusiestTasksShareTheLastRow() {
        assertThat(Solution.minSlots("AABB", 2)).as("minSlots of AABB with cooldown 2").isEqualTo(5);
    }

    @Test
    void hugeCooldownStretchesTwoTasks() {
        assertThat(Solution.minSlots("AA", 50)).as("minSlots of AA with cooldown 50").isEqualTo(52);
    }

    @Test
    void fillersExceedTheFrame() {
        assertThat(Solution.minSlots("AAAAAABCDEFG", 2)).as("minSlots of AAAAAABCDEFG with cooldown 2").isEqualTo(16);
    }

    @Test
    void moreTasksThanTheFrameHolds() {
        assertThat(Solution.minSlots("AABBCCDD", 1)).as("minSlots of AABBCCDD with cooldown 1").isEqualTo(8);
    }

    @Test
    void rejectsNegativeCooldown() {
        assertThatThrownBy(() -> Solution.minSlots("AB", -1)).as("negative cooldown").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void handlesLongRunsOfOneTask() {
        String tasks = "Z".repeat(1000) + "Y".repeat(10);
        assertThat(Solution.minSlots(tasks, 3)).as("1000 Z and 10 Y with cooldown 3").isEqualTo(3997);
    }
}
