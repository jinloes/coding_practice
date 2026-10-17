package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void samplesTwoValues(){ int[] v={0,1,2,3}; Solution.samplePrefix(v,2,new Random(1)); assertValid(v,new int[]{0,1,2,3},2); }
    @Test
    void samplesZeroValues(){ int[] v={5,6,7}; Solution.samplePrefix(v,0,new Random(1)); assertValid(v,new int[]{5,6,7},0); }
    @Test
    void samplesEveryValue(){ int[] v={8,9}; Solution.samplePrefix(v,2,new Random(1)); assertValid(v,new int[]{8,9},2); } private static void assertValid(int[] values,int[] original,int k){ assertThat(values).containsExactlyInAnyOrder(original); assertThat(Arrays.stream(Arrays.copyOf(values,k)).distinct().count()).isEqualTo(k); } }

