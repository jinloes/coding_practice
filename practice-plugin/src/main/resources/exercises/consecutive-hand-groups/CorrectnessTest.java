package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyHandSplitsTrivially() {
        check(new int[]{}, 3, true);
    }

    @Test
    void lengthNotMultipleOfGroupSize() {
        check(new int[]{1, 2, 3, 4}, 3, false);
    }

    @Test
    void duplicatesStartOverlappingRuns() {
        check(new int[]{1, 2, 2, 3, 3, 4}, 3, true);
    }

    @Test
    void unorderedInputIsFine() {
        check(new int[]{3, 1, 2, 5, 4, 6}, 3, true);
    }

    @Test
    void sameCountsButWrongShape() {
        check(new int[]{1, 2, 3, 3, 3, 4}, 3, false);
    }

    @Test
    void negativeValues() {
        check(new int[]{-2, -1, 0, 1}, 2, true);
    }

    @Test
    void groupSizeOneAcceptsAnything() {
        check(new int[]{7, 7, 3, 9}, 1, true);
    }

    @Test
    void extremeValuesDoNotOverflow() {
        check(new int[]{Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MAX_VALUE}, 3, false);
    }

    @Test
    void manyDuplicatesOfOneRun() {
        check(repeatRun(100), 4, true);
    }

    private static int[] repeatRun(int copies) {
        int[] cards = new int[copies * 4];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = 10 + i % 4;
        }
        return cards;
    }

    @Test
    void rejectsNonPositiveGroupSize() {
        assertThatThrownBy(() -> Solution.canGroup(new int[]{1, 2}, 0)).as("canGroup([1, 2], 0)")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Solution.canGroup(new int[]{}, -1)).as("canGroup([], -1)")
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static void check(int[] cards, int groupSize, boolean expected) {
        int[] copy = cards.clone();
        String call = "canGroup(%s, %d)".formatted(Arrays.toString(cards), groupSize);
        assertThat(Solution.canGroup(cards, groupSize)).as(call).isEqualTo(expected);
        assertThat(cards).as(call + " must not modify its input").containsExactly(copy);
    }
}
