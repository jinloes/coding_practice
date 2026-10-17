package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void choosesShortestAvailableTask() { assertThat(Solution.processingOrder(new int[][]{{1,2},{2,4},{3,2},{4,1}})).containsExactly(0,2,3,1); }
    @Test
    void breaksTiesByIndex() { assertThat(Solution.processingOrder(new int[][]{{7,10},{7,12},{7,5},{7,4},{7,2}})).containsExactly(4,3,2,0,1); }
    @Test
    void handlesOneTask() { assertThat(Solution.processingOrder(new int[][]{{5,3}})).containsExactly(0); }
}
