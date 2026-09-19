package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesAnEmptyArray() {
        assertThat(Solution.search(new int[0], 10)).isEqualTo(-1);
    }

    @Test
    void handlesAOneElementArray() {
        assertThat(Solution.search(new int[]{8}, 8)).isEqualTo(0);
        assertThat(Solution.search(new int[]{8}, 2)).isEqualTo(-1);
    }

    @Test
    void findsBothArrayBoundaries() {
        int[] sorted = {-20, -3, 0, 4, 17};
        assertValidIndex(sorted, -20);
        assertValidIndex(sorted, 17);
    }

    @Test
    void handlesNegativeAndRepeatedValues() {
        int[] sorted = {-5, -5, -2, 0, 0, 0, 12};
        assertValidIndex(sorted, -5);
        assertValidIndex(sorted, 0);
    }

    @Test
    void reportsTargetsBetweenValues() {
        int[] sorted = {-9, -2, 4, 10};
        assertThat(Solution.search(sorted, -8)).isEqualTo(-1);
        assertThat(Solution.search(sorted, 5)).isEqualTo(-1);
    }

    @Test
    void reportsTargetsOutsideBothEnds() {
        int[] sorted = {-9, -2, 4, 10};
        assertThat(Solution.search(sorted, -100)).isEqualTo(-1);
        assertThat(Solution.search(sorted, 100)).isEqualTo(-1);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] sorted = {-8, -1, 2, 7, 13};
        int[] before = sorted.clone();
        Solution.search(sorted, 2);
        assertThat(sorted).containsExactly(before);
    }

    @Test
    void handlesAWideSearchInterval() {
        int[] sorted = {-1_000_000, -100, 0, 100, 1_000_000};
        assertValidIndex(sorted, 1_000_000);
    }

    private static void assertValidIndex(int[] sorted, int target) {
        int index = Solution.search(sorted, target);
        assertThat(index).isBetween(0, sorted.length - 1);
        assertThat(sorted[index]).isEqualTo(target);
    }
}
