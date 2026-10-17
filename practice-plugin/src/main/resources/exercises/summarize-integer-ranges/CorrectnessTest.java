package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() {
        assertThat(Solution.summarize(new int[0])).isEmpty();
    }

    @Test
    void handlesSingleValue() {
        assertThat(Solution.summarize(new int[]{42})).containsExactly("42");
    }

    @Test
    void keepsSeparatedSingletons() {
        assertThat(Solution.summarize(new int[]{-5, -1, 3, 9})).containsExactly("-5", "-1", "3", "9");
    }

    @Test
    void doesNotMutateInput() {
        int[] numbers = {-2, -1, 0, 4};
        Solution.summarize(numbers);
        assertThat(numbers).containsExactly(-2, -1, 0, 4);
    }
}
