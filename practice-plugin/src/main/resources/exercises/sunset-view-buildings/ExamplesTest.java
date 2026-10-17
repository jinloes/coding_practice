package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsVisibleBuildingsInEastToWestOrder() {
        assertThat(Solution.sunsetViews(List.of(3, 7, 8, 3, 6, 1))).containsExactly(2, 4, 5);
    }

    @Test
    void equalHeightsBlockEasternBuildings() {
        assertThat(Solution.sunsetViews(List.of(5, 5, 4))).containsExactly(1, 2);
    }
}
