package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void buildsOneRow() { assertThat(Solution.rows(1)).containsExactly(List.of(1)); }
    @Test
    void buildsFiveRows() { assertThat(Solution.rows(5)).containsExactly(List.of(1), List.of(1,1), List.of(1,2,1), List.of(1,3,3,1), List.of(1,4,6,4,1)); }
    @Test
    void returnsEmptyForZeroRows() { assertThat(Solution.rows(0)).isEmpty(); }
}
