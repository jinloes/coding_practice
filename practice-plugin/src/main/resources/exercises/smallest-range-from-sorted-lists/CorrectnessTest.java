package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.*; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void breaksTiesBySmallerStart(){ assertThat(Solution.smallestRange(List.of(List.of(1,10),List.of(2,11),List.of(3,12)))).containsExactly(1,3); }
    @Test
    void handlesNegativeValues(){ assertThat(Solution.smallestRange(List.of(List.of(-10,-5,0),List.of(-6,2),List.of(-7,-4)))).containsExactly(-7,-5); }
    @Test
    void handlesOneInputList(){ assertThat(Solution.smallestRange(List.of(List.of(4,8,9)))).containsExactly(4,4); }
    @Test
    void doesNotMutateLists(){ List<List<Integer>> lists=new ArrayList<>(); lists.add(new ArrayList<>(List.of(4,10))); lists.add(new ArrayList<>(List.of(5,11))); List<List<Integer>> before=lists.stream().map(List::copyOf).toList(); Solution.smallestRange(lists); assertThat(lists).containsExactlyElementsOf(before); }
    @Test
    void handlesSeededManyLists(){ List<List<Integer>> lists=new ArrayList<>(); for(int i=0;i<40;i++){ List<Integer> list=new ArrayList<>(); for(int j=0;j<20;j++) list.add(i+j*50); lists.add(list); } int[] range=Solution.smallestRange(lists); assertThat(range).hasSize(2); assertThat(range[1]-range[0]).isLessThanOrEqualTo(39); } }
