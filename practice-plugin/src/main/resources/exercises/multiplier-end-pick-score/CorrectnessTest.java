package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void permitsPickingAllNumbers() { assertThat(Solution.maximumScore(new int[]{2,9}, new int[]{3,4})).isEqualTo(42); }
    @Test
    void canPreferSmallerImmediateGain() { assertThat(Solution.maximumScore(new int[]{10,-1,8}, new int[]{1,10})).isEqualTo(108); }
    @Test
    void handlesNegativeMultipliers() { assertThat(Solution.maximumScore(new int[]{-8,5,-4,2}, new int[]{-3,6,-2})).isEqualTo(62); }
    @Test
    void doesNotMutateInputs() { int[] n={3,1,5,9}; int[] m={2,4}; Solution.maximumScore(n,m); assertThat(n).containsExactly(3,1,5,9); assertThat(m).containsExactly(2,4); }
    @Test
    void matchesBruteForceOnSmallGeneratedCase() { int[] n={-2,7,1,-5,4}; int[] m={3,-4,2}; assertThat(Solution.maximumScore(n,m)).isEqualTo(brute(n,m,0,n.length-1,0)); }
    private static int brute(int[] n,int[] m,int l,int r,int i){ if(i==m.length)return 0; return Math.max(m[i]*n[l]+brute(n,m,l+1,r,i+1), m[i]*n[r]+brute(n,m,l,r-1,i+1)); }
}
