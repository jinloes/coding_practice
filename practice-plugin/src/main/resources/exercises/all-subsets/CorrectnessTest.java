package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleValue() {
        assertThat(show(Solution.subsets(new int[]{7})))
                .containsExactlyInAnyOrder("[]", "[7]");
    }

    @Test
    void unsortedInput() {
        assertThat(show(Solution.subsets(new int[]{9, 2, 5})))
                .containsExactlyInAnyOrder("[]", "[2]", "[5]", "[9]", "[2, 5]", "[2, 9]", "[5, 9]", "[2, 5, 9]");
    }

    @Test
    void fourValues() {
        assertThat(show(Solution.subsets(new int[]{4, 3, 2, 1})))
                .containsExactlyInAnyOrder("[]", "[1]", "[2]", "[3]", "[4]", "[1, 2]", "[1, 3]", "[1, 4]", "[2, 3]", "[2, 4]", "[3, 4]", "[1, 2, 3]", "[1, 2, 4]", "[1, 3, 4]", "[2, 3, 4]", "[1, 2, 3, 4]");
    }

    @Test
    void includesZeroAndNegatives() {
        assertThat(show(Solution.subsets(new int[]{0, -3, 3})))
                .containsExactlyInAnyOrder("[]", "[-3]", "[0]", "[3]", "[-3, 0]", "[-3, 3]", "[0, 3]", "[-3, 0, 3]");
    }

    @Test
    void fiveValues() {
        assertThat(show(Solution.subsets(new int[]{10, 20, 30, 40, 50})))
                .containsExactlyInAnyOrder("[]", "[10]", "[20]", "[30]", "[40]", "[50]", "[10, 20]", "[10, 30]", "[10, 40]", "[10, 50]", "[20, 30]", "[20, 40]", "[20, 50]", "[30, 40]", "[30, 50]", "[40, 50]", "[10, 20, 30]", "[10, 20, 40]", "[10, 20, 50]", "[10, 30, 40]", "[10, 30, 50]", "[10, 40, 50]", "[20, 30, 40]", "[20, 30, 50]", "[20, 40, 50]", "[30, 40, 50]", "[10, 20, 30, 40]", "[10, 20, 30, 50]", "[10, 20, 40, 50]", "[10, 30, 40, 50]", "[20, 30, 40, 50]", "[10, 20, 30, 40, 50]");
    }

    @Test
    void largeAndSmallMagnitudes() {
        assertThat(show(Solution.subsets(new int[]{1000000, -1000000})))
                .containsExactlyInAnyOrder("[]", "[-1000000]", "[1000000]", "[-1000000, 1000000]");
    }

    @Test
    void doesNotMutateTheInput() {
        int[] values = {3, 1, 2};
        Solution.subsets(values);
        assertThat(values).containsExactly(3, 1, 2);
    }

    @Test
    void returnsIndependentLists() {
        List<List<Integer>> result = Solution.subsets(new int[]{1, 2});
        result.get(0).add(99);
        assertThat(show(Solution.subsets(new int[]{1, 2}))).hasSize(4);
        long withNinetyNine = result.stream().filter(list -> list.contains(99)).count();
        assertThat(withNinetyNine).as("lists containing 99 after one edit").isEqualTo(1);
    }

    @Test
    void producesTwoToTheKSubsets() {
        int[] values = new int[10];
        for (int i = 0; i < values.length; i++) {
            values[i] = i * 3;
        }
        assertThat(show(Solution.subsets(values))).hasSize(1024).doesNotHaveDuplicates();
    }

    private static List<String> show(List<List<Integer>> lists) {
        List<String> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            List<Integer> sorted = new ArrayList<>(list);
            Collections.sort(sorted);
            result.add(sorted.toString());
        }
        return result;
    }
}
