package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.Random; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void allowsEitherColorToStandBehind(){ assertThat(Solution.canTakePhoto(new int[]{10,11,12},new int[]{1,2,3})).isTrue(); }
    @Test
    void equalHeightsBlockThePhoto(){ assertThat(Solution.canTakePhoto(new int[]{1,2,3},new int[]{2,3,3})).isFalse(); }
    @Test
    void emptyTeamsAreFeasible(){ assertThat(Solution.canTakePhoto(new int[0],new int[0])).isTrue(); }
    @Test
    void doesNotMutateInputs(){ int[] red={9,1,5}; int[] blue={10,6,2}; int[] r=red.clone(), b=blue.clone(); Solution.canTakePhoto(red,blue); assertThat(red).containsExactly(r); assertThat(blue).containsExactly(b); }
    @Test
    void handlesSeededLargeTeams(){ Random random=new Random(3); int[] front=new int[300]; int[] back=new int[300]; for(int i=0;i<front.length;i++){ front[i]=random.nextInt(1000); back[i]=front[i]+1000; } assertThat(Solution.canTakePhoto(front,back)).isTrue(); } }
