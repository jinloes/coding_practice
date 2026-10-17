package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList; import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void splitsEvenLengthList(){ assertThat(Solution.bucketByPosition(list(1,2,3,4))).containsExactly(List.of(1,3), List.of(2,4)); }
    @Test
    void keepsDuplicateValues(){ assertThat(Solution.bucketByPosition(list(7,7,8,8,9))).containsExactly(List.of(7,8,9), List.of(7,8)); }
    @Test
    void handlesNegativeValues(){ assertThat(Solution.bucketByPosition(list(-1,-2,-3))).containsExactly(List.of(-1,-3), List.of(-2)); }
    @Test
    void doesNotMutateNodes(){ Solution.Node head=list(1,2,3); Solution.bucketByPosition(head); assertThat(values(head)).containsExactly(1,2,3); }
    @Test
    void splitsLargeList(){ Solution.Node head=range(40); assertThat(Solution.bucketByPosition(head).get(0)).hasSize(20).startsWith(0,2,4); } private static Solution.Node range(int n){ Solution.Node head=null; for(int i=n-1;i>=0;i--) head=new Solution.Node(i,head); return head; } private static Solution.Node list(int... values){ Solution.Node head=null; for(int i=values.length-1;i>=0;i--) head=new Solution.Node(values[i],head); return head; } private static List<Integer> values(Solution.Node head){ ArrayList<Integer> out=new ArrayList<>(); for(Solution.Node n=head;n!=null;n=n.next) out.add(n.value); return out; } }

