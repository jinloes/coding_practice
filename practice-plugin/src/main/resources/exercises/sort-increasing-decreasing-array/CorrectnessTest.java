package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyArrayReturnsEmpty(){ assertThat(Solution.sortAlternatingRuns(new int[0])).isEmpty(); }
    @Test
    void doesNotMutateInput(){ int[] v={1,3,2}; assertThat(Solution.sortAlternatingRuns(v)).containsExactly(1,2,3); assertThat(v).containsExactly(1,3,2); }
    @Test
    void handlesNegatives(){ assertThat(Solution.sortAlternatingRuns(new int[]{-5,-2,-2,-3,-4,0,1,1,-1})).containsExactly(-5,-4,-3,-2,-2,-1,0,1,1); } }


