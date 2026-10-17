package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsProvidedValueForWeightedCase(){ assertThat(Solution.choose(new int[]{10,20,30}, new double[]{1.0,3.0,6.0}, new Random(1))).isIn(10,20,30); }
    @Test
    void alwaysReturnsTheOnlyValue(){ assertThat(Solution.choose(new int[]{7}, new double[]{5.0}, new Random(1))).isEqualTo(7); }
    @Test
    void returnsProvidedValueForUniformCase(){ assertThat(Solution.choose(new int[]{4,9}, new double[]{1.0,1.0}, new Random(2))).isIn(4,9); } }

