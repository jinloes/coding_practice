package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() { assertThat(Solution.assignPairs(new int[0])).isEmpty(); }
    @Test
    void preservesDuplicatesAndOrderContract() { assertThat(Solution.assignPairs(new int[]{7,1,7,1,4,4})).isDeepEqualTo(new int[][]{{1,7},{1,7},{4,4}}); }
    @Test
    void doesNotMutateInput() { int[] d={9,1,4,6}; Solution.assignPairs(d); assertThat(d).containsExactly(9,1,4,6); }
    @Test
    void achievesOptimalMaximumLoadForSmallCase() { int[][] r=Solution.assignPairs(new int[]{8,1,2,9,4,5}); assertThat(maxLoad(r)).isEqualTo(10); assertThat(flattenSorted(r)).containsExactly(1,2,4,5,8,9); }
    @Test
    void handlesAlreadySortedInput() { assertThat(Solution.assignPairs(new int[]{1,2,3,4,5,6})).isDeepEqualTo(new int[][]{{1,6},{2,5},{3,4}}); }
    private static int maxLoad(int[][] pairs){int max=0; for(int[] p:pairs) max=Math.max(max,p[0]+p[1]); return max;}
    private static int[] flattenSorted(int[][] pairs){int[] v=new int[pairs.length*2]; for(int i=0;i<pairs.length;i++){v[2*i]=pairs[i][0]; v[2*i+1]=pairs[i][1];} Arrays.sort(v); return v;}
}
