package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversEdgesAndMutation() { int[] a={-10,-5,0,3,9}; int[] before=a.clone(); assertThat(Solution.findFixedPoint(new int[0])).isEqualTo(-1); assertThat(Solution.findFixedPoint(a)).isEqualTo(3); assertThat(a).containsExactly(before); }
    @Test
    void findsLaterFixedPointAndMissingCase() { assertThat(Solution.findFixedPoint(new int[]{-2,-1,0,1,4,10})).isEqualTo(4); assertThat(Solution.findFixedPoint(new int[]{-7,-3,1,4,8})).isEqualTo(-1); }
}

