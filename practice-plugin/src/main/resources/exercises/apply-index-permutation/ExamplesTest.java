package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void appliesThreeCycle(){ assertResult(new int[]{10,20,30}, new int[]{2,0,1}, new int[]{20,30,10}); }
    @Test
    void appliesIdentity(){ assertResult(new int[]{4,5,6}, new int[]{0,1,2}, new int[]{4,5,6}); }
    @Test
    void appliesTwoSwaps(){ assertResult(new int[]{7,8,9,10}, new int[]{1,0,3,2}, new int[]{8,7,10,9}); } private static void assertResult(int[] values,int[] permutation,int[] expected){ int[] originalPermutation=permutation.clone(); Solution.applyPermutation(values,permutation); assertThat(values).containsExactly(expected); assertThat(permutation).containsExactly(originalPermutation); } }

