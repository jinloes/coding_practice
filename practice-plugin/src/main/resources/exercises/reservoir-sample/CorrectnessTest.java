package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays; import java.util.List; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyStream(){ assertThat(Solution.sample(List.<Integer>of().iterator(),3,new Random(1))).isEmpty(); }
    @Test
    void samplesWithoutDuplicatesFromDistinctStream(){ int[] result=Solution.sample(List.of(0,1,2,3,4,5).iterator(),4,new Random(2)); assertThat(result).hasSize(4); assertThat(Arrays.stream(result).allMatch(value -> value >= 0 && value <= 5)).isTrue(); assertThat(Arrays.stream(result).distinct().count()).isEqualTo(4); }
    @Test
    void includesLaterItemsOverManyTrials(){ int countLast=0; Random random=new Random(123); for(int t=0;t<3000;t++){ int[] sample=Solution.sample(List.of(0,1,2,3,4).iterator(),1,random); if(sample.length==1 && sample[0]==4) countLast++; } assertThat(countLast).isBetween(500,700); }
    @Test
    void approximatesUniformInclusionForKTwo(){ int[] counts=new int[5]; Random random=new Random(456); for(int t=0;t<4000;t++){ for(int value: Solution.sample(List.of(0,1,2,3,4).iterator(),2,random)) counts[value]++; } for(int count:counts) assertThat(count).isBetween(1450,1750); }
    @Test
    void returnsRequestedSizeWhenStreamIsLongEnough(){ assertThat(Solution.sample(List.of(8,9,10,11,12).iterator(),3,new Random(9))).hasSize(3); } }

