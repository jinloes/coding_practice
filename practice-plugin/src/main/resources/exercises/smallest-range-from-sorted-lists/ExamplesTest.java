package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.List; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsRangeTouchingAllLists(){ assertThat(Solution.smallestRange(List.of(List.of(4,10,15),List.of(1,12,20),List.of(6,14,24)))).containsExactly(12,15); }
    @Test
    void canHaveZeroLengthRange(){ assertThat(Solution.smallestRange(List.of(List.of(1,2,3),List.of(1,2,3)))).containsExactly(1,1); }
    @Test
    void handlesSingletonList(){ assertThat(Solution.smallestRange(List.of(List.of(5),List.of(0,10)))).containsExactly(0,5); } }
