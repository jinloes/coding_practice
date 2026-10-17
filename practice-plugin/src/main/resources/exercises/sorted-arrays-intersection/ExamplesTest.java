package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void removesDuplicateSharedValues(){ assertThat(Solution.intersection(new int[]{1,2,2,3},new int[]{2,2,4})).containsExactly(2); }
    @Test
    void returnsEmptyWhenDisjoint(){ assertThat(Solution.intersection(new int[]{1,3,5},new int[]{2,4,6})).isEmpty(); }
    @Test
    void handlesNegativeSharedValues(){ assertThat(Solution.intersection(new int[]{-3,-1,0,4},new int[]{-5,-3,0,2})).containsExactly(-3,0); } }
