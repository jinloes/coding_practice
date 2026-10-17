package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void usesWeightAndAvailability(){ assertThat(Solution.assignTasks(new int[]{3,3,2},new int[]{1,2,3,2,1,2})).containsExactly(2,2,0,2,1,2); }
    @Test
    void singleServerDoesAllWork(){ assertThat(Solution.assignTasks(new int[]{5},new int[]{2,1,3})).containsExactly(0,0,0); } }


