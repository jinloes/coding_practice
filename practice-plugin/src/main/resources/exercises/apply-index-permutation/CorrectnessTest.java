package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyArrays(){ assertResult(new int[]{}, new int[]{}, new int[]{}); }
    @Test
    void handlesSingleElement(){ assertResult(new int[]{42}, new int[]{0}, new int[]{42}); }
    @Test
    void handlesMultipleCycles(){ assertResult(new int[]{1,2,3,4,5,6}, new int[]{2,0,1,5,3,4}, new int[]{2,3,1,5,6,4}); }
    @Test
    void handlesNegativeValues(){ assertResult(new int[]{-1,-2,-3,-4}, new int[]{3,2,1,0}, new int[]{-4,-3,-2,-1}); }
    @Test
    void handlesLongerRotation(){ assertResult(new int[]{0,1,2,3,4,5,6,7}, new int[]{1,2,3,4,5,6,7,0}, new int[]{7,0,1,2,3,4,5,6}); } private static void assertResult(int[] values,int[] permutation,int[] expected){ int[] originalPermutation=permutation.clone(); Solution.applyPermutation(values,permutation); assertThat(values).containsExactly(expected); assertThat(permutation).containsExactly(originalPermutation); } }

