package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void advancesInsideASuffix(){ assertPermutation(new int[]{1,4,3,2}, true, new int[]{2,1,3,4}); }
    @Test
    void handlesEmptyArray(){ assertPermutation(new int[]{}, false, new int[]{}); }
    @Test
    void handlesSingleValue(){ assertPermutation(new int[]{9}, false, new int[]{9}); }
    @Test
    void advancesWithRepeatedSuffixValues(){ assertPermutation(new int[]{2,2,3,1}, true, new int[]{2,3,1,2}); }
    @Test
    void advancesALargerArrangement(){ assertPermutation(new int[]{1,2,4,3,3}, true, new int[]{1,3,2,3,4}); } private static void assertPermutation(int[] values, boolean expected, int[] after){ assertThat(Solution.nextPermutation(values)).isEqualTo(expected); assertThat(values).containsExactly(after); } }

