package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void zeroRequested(){ assertThat(Solution.closestValues(new int[]{1,2,3},0,2)).isEmpty(); }
    @Test
    void rightEdge(){ assertThat(Solution.closestValues(new int[]{1,4,8,9,10},3,100)).containsExactly(8,9,10); }
    @Test
    void doesNotMutate(){ int[] v={1,2,8}; assertThat(Solution.closestValues(v,2,7)).containsExactly(2,8); assertThat(v).containsExactly(1,2,8); } }


