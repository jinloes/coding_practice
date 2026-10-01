package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void handlesAWindowCoveringTheWholeArray() { assertThat(Solution.maxSlidingWindow(new int[]{-4, 2, 0, -1}, 4)).containsExactly(2); }
    @Test
    void handlesDuplicateAndNegativeMaxima() { assertThat(Solution.maxSlidingWindow(new int[]{-2, -2, -3, -1, -1}, 3)).containsExactly(-2, -1, -1); }
    @Test
    void handlesAlternatingPeaks() { assertThat(Solution.maxSlidingWindow(new int[]{5, 1, 5, 1, 5}, 2)).containsExactly(5, 5, 5, 5); }
    @Test
    void doesNotMutateInput() {
        int[] numbers = {4, 1, 3, 2};
        int[] before = numbers.clone();
        Solution.maxSlidingWindow(numbers, 2);
        assertThat(numbers).containsExactly(before);
    }
    @Test
    void agreesWithBruteForce() {
        int[] numbers = {3, -1, 4, 2, 0, 5, -2};
        int window = 4;
        int[] expected = new int[numbers.length - window + 1];
        for (int start = 0; start < expected.length; start++) {
            expected[start] = numbers[start];
            for (int i = start + 1; i < start + window; i++) expected[start] = Math.max(expected[start], numbers[i]);
        }
        assertThat(Solution.maxSlidingWindow(numbers, window)).containsExactly(expected);
    }
}
