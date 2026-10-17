package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays; import java.util.Random; import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void keepsArrayMultisetForLargerInput(){ int[] original=range(30); int[] values=original.clone(); Solution.samplePrefix(values,7,new Random(2)); assertValid(values,original,7); }
    @Test
    void handlesSingleElementSample(){ int[] values={42}; Solution.samplePrefix(values,1,new Random(3)); assertThat(values).containsExactly(42); }
    @Test
    void canMoveValuesFromTheTailIntoThePrefix(){ int[] values=range(8); Solution.samplePrefix(values,3,new Random(5)); assertValid(values,range(8),3); }
    @Test
    void firstValueIsApproximatelyUniform(){ int[] counts=new int[5]; Random random=new Random(123); for(int t=0;t<5000;t++){ int[] values=range(5); Solution.samplePrefix(values,1,random); counts[values[0]]++; } for(int count:counts) assertThat(count).isBetween(850,1150); }
    @Test
    void everyPrefixValueBelongsToTheOriginalArray(){ int[] values={11,13,17,19,23,29}; Solution.samplePrefix(values,4,new Random(9)); Set<Integer> original=Set.of(11,13,17,19,23,29); for(int i=0;i<4;i++) assertThat(original).contains(values[i]); } private static int[] range(int n){ int[] v=new int[n]; for(int i=0;i<n;i++) v[i]=i; return v; } private static void assertValid(int[] values,int[] original,int k){ assertThat(values).containsExactlyInAnyOrder(original); assertThat(Arrays.stream(Arrays.copyOf(values,k)).distinct().count()).isEqualTo(k); } }

