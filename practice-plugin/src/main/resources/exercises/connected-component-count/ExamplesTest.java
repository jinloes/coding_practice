package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void nodesWithoutEdgesAreSeparateComponents() {
        assertThat(Solution.countComponents(4, new int[0][2])).isEqualTo(4);
    }

    @Test
    void countsTwoGroups() {
        assertThat(Solution.countComponents(5, new int[][]{{0, 1}, {1, 2}, {3, 4}})).isEqualTo(2);
    }

    @Test
    void cycleStillFormsOneComponent() {
        assertThat(Solution.countComponents(3, new int[][]{{0, 1}, {1, 2}, {2, 0}})).isEqualTo(1);
    }
}
