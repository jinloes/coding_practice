package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void advancesAscendingArray(){ assertPermutation(new int[]{1,2,3}, true, new int[]{1,3,2}); }
    @Test
    void wrapsDescendingArray(){ assertPermutation(new int[]{3,2,1}, false, new int[]{1,2,3}); }
    @Test
    void handlesDuplicateValues(){ assertPermutation(new int[]{1,1,5}, true, new int[]{1,5,1}); } private static void assertPermutation(int[] values, boolean expected, int[] after){ assertThat(Solution.nextPermutation(values)).isEqualTo(expected); assertThat(values).containsExactly(after); } }

