package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.Arrays; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void handlesEmptySides(){ assertThat(Solution.intersection(new int[0],new int[]{1,2})).isEmpty(); assertThat(Solution.intersection(new int[]{1},new int[0])).isEmpty(); }
    @Test
    void collapsesLongDuplicateRuns(){ assertThat(Solution.intersection(new int[]{1,1,1,2,2,3},new int[]{1,1,2,2,2,4})).containsExactly(1,2); }
    @Test
    void includesIntegerBoundaries(){ assertThat(Solution.intersection(new int[]{Integer.MIN_VALUE,0,Integer.MAX_VALUE},new int[]{Integer.MIN_VALUE,Integer.MAX_VALUE})).containsExactly(Integer.MIN_VALUE,Integer.MAX_VALUE); }
    @Test
    void doesNotMutateInputs(){ int[] a={1,2,2,5}; int[] b={2,5,5}; int[] ac=a.clone(); int[] bc=b.clone(); Solution.intersection(a,b); assertThat(a).containsExactly(ac); assertThat(b).containsExactly(bc); }
    @Test
    void handlesSeededLargeArrays(){ int[] a=new int[500]; int[] b=new int[700]; for(int i=0;i<a.length;i++)a[i]=i/3; for(int i=0;i<b.length;i++)b[i]=50+i/4; int[] result=Solution.intersection(a,b); assertThat(result).isSorted().doesNotHaveDuplicates(); assertThat(Arrays.stream(result).allMatch(v->v>=50&&v<=166)).isTrue(); } }
