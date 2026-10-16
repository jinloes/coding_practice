package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void splitsIntoTwoRuns() {
        check(new int[]{4, 5, 6, 9, 10, 11}, 3, true);
    }

    @Test
    void gapBreaksTheSplit() {
        check(new int[]{1, 2, 3, 5}, 2, false);
    }

    @Test
    void singleCardsAreTheirOwnRuns() {
        check(new int[]{8}, 1, true);
    }

    private static void check(int[] cards, int groupSize, boolean expected) {
        int[] copy = cards.clone();
        String call = "canGroup(%s, %d)".formatted(Arrays.toString(cards), groupSize);
        assertThat(Solution.canGroup(cards, groupSize)).as(call).isEqualTo(expected);
        assertThat(cards).as(call + " must not modify its input").containsExactly(copy);
    }
}
