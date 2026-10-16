package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noStonesWeighNothing() {
        assertThat(Solution.smash(new int[]{})).as("smash of []").isEqualTo(0);
    }

    @Test
    void everythingCancelsToZero() {
        assertThat(Solution.smash(new int[]{7, 4, 9, 2})).as("smash of [7, 4, 9, 2]").isEqualTo(0);
    }

    @Test
    void differenceFeedsBackIntoThePile() {
        assertThat(Solution.smash(new int[]{100, 1, 1, 1})).as("smash of [100, 1, 1, 1]").isEqualTo(97);
    }

    @Test
    void oddCountOfEqualStones() {
        assertThat(Solution.smash(new int[]{5, 5, 5})).as("smash of [5, 5, 5]").isEqualTo(5);
    }

    @Test
    void twoStonesLeaveTheirDifference() {
        assertThat(Solution.smash(new int[]{10, 4})).as("smash of [10, 4]").isEqualTo(6);
    }

    @Test
    void mixedWeights() {
        assertThat(Solution.smash(new int[]{3, 9, 4, 4, 1})).as("smash of [3, 9, 4, 4, 1]").isEqualTo(1);
    }

    @Test
    void leavesTheInputUntouched() {
        int[] stones = {2, 9, 4};
        Solution.smash(stones);
        assertThat(stones).as("stones after smash").containsExactly(2, 9, 4);
    }

    @Test
    void matchesRepeatedSortingOnPseudoRandomPiles() {
        Random random = new Random(5);
        for (int round = 0; round < 200; round++) {
            int[] stones = random.ints(random.nextInt(12), 1, 30).toArray();
            List<Integer> pile = new ArrayList<>();
            for (int stone : stones) {
                pile.add(stone);
            }
            while (pile.size() > 1) {
                Collections.sort(pile);
                int first = pile.remove(pile.size() - 1);
                int second = pile.remove(pile.size() - 1);
                if (first != second) {
                    pile.add(first - second);
                }
            }
            int expected = pile.isEmpty() ? 0 : pile.get(0);
            assertThat(Solution.smash(stones)).as("smash of " + java.util.Arrays.toString(stones)).isEqualTo(expected);
        }
    }
}
