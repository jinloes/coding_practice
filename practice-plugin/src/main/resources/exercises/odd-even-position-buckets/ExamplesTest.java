package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsFiveValues(){ assertThat(Solution.bucketByPosition(list(10,11,12,13,14))).containsExactly(List.of(10,12,14), List.of(11,13)); }
    @Test
    void handlesOneNode(){ assertThat(Solution.bucketByPosition(list(5))).containsExactly(List.of(5), List.of()); }
    @Test
    void handlesEmptyList(){ assertThat(Solution.bucketByPosition(null)).containsExactly(List.of(), List.of()); } private static Solution.Node list(int... values){ Solution.Node head=null; for(int i=values.length-1;i>=0;i--) head=new Solution.Node(values[i],head); return head; } }

