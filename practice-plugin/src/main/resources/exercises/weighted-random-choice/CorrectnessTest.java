package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.HashMap; import java.util.Map; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void approximatesTheRequestedDistribution(){ int[] values={10,20,30}; double[] weights={1.0,3.0,6.0}; Map<Integer,Integer> counts=new HashMap<>(); Random random=new Random(12345); for(int i=0;i<20000;i++) counts.merge(Solution.choose(values,weights,random),1,Integer::sum); assertThat(counts.getOrDefault(10,0)).isBetween(1500,2500); assertThat(counts.getOrDefault(20,0)).isBetween(5200,6800); assertThat(counts.getOrDefault(30,0)).isBetween(11200,12800); }
    @Test
    void stronglyBiasedFirstWeightWinsForLowRandomDraw(){ assertThat(Solution.choose(new int[]{1,2}, new double[]{100.0,1.0}, new FixedRandom(0.1))).isEqualTo(1); }
    @Test
    void canChooseTheLastRange(){ assertThat(Solution.choose(new int[]{1,2,3}, new double[]{1.0,1.0,1.0}, new FixedRandom(0.9999))).isEqualTo(3); }
    @Test
    void canChooseTheFirstRange(){ assertThat(Solution.choose(new int[]{1,2,3}, new double[]{1.0,1.0,1.0}, new FixedRandom(0.0))).isEqualTo(1); }
    @Test
    void doesNotMutateInputs(){ int[] values={5,6}; double[] weights={2.0,8.0}; Solution.choose(values,weights,new Random(1)); assertThat(values).containsExactly(5,6); assertThat(weights).containsExactly(2.0,8.0); } static class FixedRandom extends Random { private final double value; FixedRandom(double value){ this.value=value; } @Override public double nextDouble(){ return value; } } }

