package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void sortsDisplacedByTwo(){ assertThat(Solution.sortNearlySorted(List.of(3,1,2,6,4,5),2)).containsExactly(1,2,3,4,5,6); }
    @Test
    void kMayExceedLength(){ assertThat(Solution.sortNearlySorted(List.of(2,1),5)).containsExactly(1,2); } }


