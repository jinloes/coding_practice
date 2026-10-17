package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void pairsShortestWithLongest() { assertThat(Solution.assignPairs(new int[]{5,2,1,6,4,4})).isDeepEqualTo(new int[][]{{1,6},{2,5},{4,4}}); }
    @Test
    void handlesFourTasks() { assertThat(Solution.assignPairs(new int[]{1,8,9,2})).isDeepEqualTo(new int[][]{{1,9},{2,8}}); }
    @Test
    void handlesOnePair() { assertThat(Solution.assignPairs(new int[]{3,3})).isDeepEqualTo(new int[][]{{3,3}}); }
}
