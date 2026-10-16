package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void smallerValuesNeverDisturbTheAnswer() {
        Solution stream = new Solution(3, new int[]{50, 40, 30});
        assertThat(stream.add(1)).as("k=3 over [50, 40, 30]: add(1)").isEqualTo(30);
        assertThat(stream.add(2)).as("k=3 over [50, 40, 30]: add(2)").isEqualTo(30);
        assertThat(stream.add(3)).as("k=3 over [50, 40, 30]: add(3)").isEqualTo(30);
    }

    @Test
    void largerValuesPushOutTheOldKth() {
        Solution stream = new Solution(3, new int[]{5, 6, 7});
        assertThat(stream.add(8)).as("k=3 over [5, 6, 7]: add(8)").isEqualTo(6);
        assertThat(stream.add(9)).as("k=3 over [5, 6, 7]: add(9)").isEqualTo(7);
        assertThat(stream.add(10)).as("k=3 over [5, 6, 7]: add(10)").isEqualTo(8);
    }

    @Test
    void startsWithKMinusOneValues() {
        Solution stream = new Solution(3, new int[]{9, 4});
        assertThat(stream.add(6)).as("k=3 over [9, 4]: add(6)").isEqualTo(4);
        assertThat(stream.add(100)).as("k=3 over [9, 4]: add(100)").isEqualTo(6);
    }

    @Test
    void negativeAndZeroValues() {
        Solution stream = new Solution(2, new int[]{-3, 0, -8});
        assertThat(stream.add(-1)).as("k=2 over [-3, 0, -8]: add(-1)").isEqualTo(-1);
        assertThat(stream.add(-2)).as("k=2 over [-3, 0, -8]: add(-2)").isEqualTo(-1);
        assertThat(stream.add(5)).as("k=2 over [-3, 0, -8]: add(5)").isEqualTo(0);
    }

    @Test
    void startingValuesNeedNotBeSorted() {
        Solution stream = new Solution(4, new int[]{20, 1, 15, 8, 3, 30});
        assertThat(stream.add(2)).as("k=4 over [20, 1, 15, 8, 3, 30]: add(2)").isEqualTo(8);
        assertThat(stream.add(16)).as("k=4 over [20, 1, 15, 8, 3, 30]: add(16)").isEqualTo(15);
    }

    @Test
    void duplicatesOfTheKthValueStillCount() {
        Solution stream = new Solution(3, new int[]{7, 7, 7});
        assertThat(stream.add(7)).as("k=3 over [7, 7, 7]: add(7)").isEqualTo(7);
        assertThat(stream.add(8)).as("k=3 over [7, 7, 7]: add(8)").isEqualTo(7);
        assertThat(stream.add(7)).as("k=3 over [7, 7, 7]: add(7)").isEqualTo(7);
    }

    @Test
    void doesNotModifyTheStartingArray() {
        int[] initial = {9, 1, 5};
        Solution stream = new Solution(2, initial);
        stream.add(7);
        assertThat(initial).as("starting array after construction and add").containsExactly(9, 1, 5);
    }

    @Test
    void rejectsInvalidConstruction() {
        assertThatThrownBy(() -> new Solution(0, new int[]{1})).as("k = 0").isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(4, new int[]{1, 2})).as("too few starting values").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void matchesBruteForceOnPseudoRandomStream() {
        java.util.Random random = new java.util.Random(11);
        int k = 5;
        int[] initial = random.ints(4, -20, 20).toArray();
        java.util.List<Integer> all = new java.util.ArrayList<>();
        for (int value : initial) {
            all.add(value);
        }
        Solution stream = new Solution(k, initial);
        for (int i = 0; i < 300; i++) {
            int value = random.nextInt(40) - 20;
            all.add(value);
            java.util.List<Integer> sorted = new java.util.ArrayList<>(all);
            sorted.sort(java.util.Collections.reverseOrder());
            assertThat(stream.add(value)).as("add(" + value + ") on step " + i).isEqualTo(sorted.get(k - 1));
        }
    }
}
