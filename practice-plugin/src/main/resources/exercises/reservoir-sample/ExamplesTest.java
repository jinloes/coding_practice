package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays; import java.util.List; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void samplesTwoValues(){ int[] result=Solution.sample(List.of(0,1,2,3).iterator(),2,new Random(1)); assertThat(result).hasSize(2); assertThat(Arrays.stream(result).allMatch(value -> value >= 0 && value <= 3)).isTrue(); assertThat(Arrays.stream(result).distinct().count()).isEqualTo(2); }
    @Test
    void returnsShortStreamsCompletely(){ assertThat(Solution.sample(List.of(4,5).iterator(),5,new Random(1))).containsExactly(4,5); }
    @Test
    void handlesZeroSampleSize(){ assertThat(Solution.sample(List.of(9,10).iterator(),0,new Random(1))).isEmpty(); } }

