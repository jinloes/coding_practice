package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergesInterleavedArrays() { int[] first={1,2,3,0,0,0}; Solution.merge(first,3,new int[]{2,5,6},3); assertThat(first).startsWith(1,2,2,3,5,6); }
    @Test
    void insertsAllSecondValuesBeforeFirst() { int[] first={4,0,0}; Solution.merge(first,1,new int[]{1,3},2); assertThat(first).startsWith(1,3,4); }
    @Test
    void handlesEmptySecondArray() { int[] first={1,2,3}; Solution.merge(first,3,new int[0],0); assertThat(first).startsWith(1,2,3); }
}

