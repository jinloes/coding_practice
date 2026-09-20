package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void handlesAnEmptyArray() {
        assertAbsent(new int[0], 10);
    }

    @Test
    void handlesAOneElementArray() {
        assertValidIndex(new int[]{8}, 8);
        assertAbsent(new int[]{8}, 2);
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
        assertAbsent(sorted, -8);
        assertAbsent(sorted, 5);
    }

    @Test
    void reportsTargetsOutsideBothEnds() {
        int[] sorted = {-9, -2, 4, 10};
        assertAbsent(sorted, -100);
        assertAbsent(sorted, 100);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] sorted = {-8, -1, 2, 7, 13};
        int[] before = sorted.clone();
        Solution.search(sorted, 2);
        assertThat(sorted).as("search must not mutate %s", Arrays.toString(before))
                .containsExactly(before);
    }

    @Test
    void handlesAWideSearchInterval() {
        assertValidIndex(new int[]{-1_000_000, -100, 0, 100, 1_000_000}, 1_000_000);
    }

    private static void assertAbsent(int[] sorted, int target) {
        assertThat(search(sorted, target))
                .as("%s must return -1 because the target is absent", call(sorted, target))
                .isEqualTo(-1);
    }

    private static void assertValidIndex(int[] sorted, int target) {
        String call = call(sorted, target);
        int index = search(sorted, target);
        assertThat(index).as("%s must return an in-bounds index", call)
                .isBetween(0, sorted.length - 1);
        assertThat(sorted[index]).as("%s returned index %d", call, index).isEqualTo(target);
    }

    private static int search(int[] sorted, int target) {
        try {
            return Solution.search(sorted, target);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(sorted, target), thrown), thrown);
        }
    }

    private static String call(int[] sorted, int target) {
        return "search(%s, %d)".formatted(Arrays.toString(sorted), target);
    }
}
