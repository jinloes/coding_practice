package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyList(){ assertThat(Solution.sortNearlySorted(List.of(),3)).isEmpty(); }
    @Test
    void zeroDistanceKeepsOrder(){ assertThat(Solution.sortNearlySorted(List.of(1,2,3),0)).containsExactly(1,2,3); }
    @Test
    void doesNotMutate(){ List<Integer> v=new ArrayList<>(List.of(4,1,3,2)); assertThat(Solution.sortNearlySorted(v,3)).containsExactly(1,2,3,4); assertThat(v).containsExactly(4,1,3,2); } }


