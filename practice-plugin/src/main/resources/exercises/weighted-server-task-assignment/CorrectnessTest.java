package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void breaksWeightTiesByIndex(){ assertThat(Solution.assignTasks(new int[]{1,1,1},new int[]{5,1,1,1})).containsExactly(0,1,1,1); }
    @Test
    void jumpsWhenAllBusy(){ assertThat(Solution.assignTasks(new int[]{10,1},new int[]{10,10,1,1})).containsExactly(1,0,1,1); }
    @Test
    void noTasksReturnsEmpty(){ assertThat(Solution.assignTasks(new int[]{2,1},new int[0])).isEmpty(); } }



