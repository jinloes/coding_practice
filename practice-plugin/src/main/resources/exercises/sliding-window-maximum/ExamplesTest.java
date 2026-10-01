package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsEachWindowMaximum() { assertThat(Solution.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)).containsExactly(3, 3, 5, 5, 6, 7); }
    @Test
    void handlesOneElement() { assertThat(Solution.maxSlidingWindow(new int[]{1}, 1)).containsExactly(1); }
    @Test
    void handlesDescendingValues() { assertThat(Solution.maxSlidingWindow(new int[]{9, 8, 7, 6}, 2)).containsExactly(9, 8, 7); }
}
