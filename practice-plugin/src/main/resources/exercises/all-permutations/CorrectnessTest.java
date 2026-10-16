package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleValue() {
        assertThat(show(Solution.permutations(new int[]{7})))
                .containsExactlyInAnyOrder("[7]");
    }

    @Test
    void keepsGivenValuesNotSortedOnes() {
        assertThat(show(Solution.permutations(new int[]{3, 1, 2})))
                .containsExactlyInAnyOrder("[3, 1, 2]", "[3, 2, 1]", "[1, 3, 2]", "[1, 2, 3]", "[2, 3, 1]", "[2, 1, 3]");
    }

    @Test
    void fourValues() {
        assertThat(show(Solution.permutations(new int[]{1, 2, 3, 4})))
                .containsExactlyInAnyOrder("[1, 2, 3, 4]", "[1, 2, 4, 3]", "[1, 3, 2, 4]", "[1, 3, 4, 2]", "[1, 4, 2, 3]", "[1, 4, 3, 2]", "[2, 1, 3, 4]", "[2, 1, 4, 3]", "[2, 3, 1, 4]", "[2, 3, 4, 1]", "[2, 4, 1, 3]", "[2, 4, 3, 1]", "[3, 1, 2, 4]", "[3, 1, 4, 2]", "[3, 2, 1, 4]", "[3, 2, 4, 1]", "[3, 4, 1, 2]", "[3, 4, 2, 1]", "[4, 1, 2, 3]", "[4, 1, 3, 2]", "[4, 2, 1, 3]", "[4, 2, 3, 1]", "[4, 3, 1, 2]", "[4, 3, 2, 1]");
    }

    @Test
    void handlesNegativeAndZero() {
        assertThat(show(Solution.permutations(new int[]{0, -1, 5})))
                .containsExactlyInAnyOrder("[0, -1, 5]", "[0, 5, -1]", "[-1, 0, 5]", "[-1, 5, 0]", "[5, 0, -1]", "[5, -1, 0]");
    }

    @Test
    void largeValues() {
        assertThat(show(Solution.permutations(new int[]{1000000, -1000000, 42})))
                .containsExactlyInAnyOrder("[1000000, -1000000, 42]", "[1000000, 42, -1000000]", "[-1000000, 1000000, 42]", "[-1000000, 42, 1000000]", "[42, 1000000, -1000000]", "[42, -1000000, 1000000]");
    }

    @Test
    void doesNotMutateTheInput() {
        int[] values = {3, 1, 2};
        Solution.permutations(values);
        assertThat(values).containsExactly(3, 1, 2);
    }

    @Test
    void producesFactorialManyDistinctOrderings() {
        assertThat(show(Solution.permutations(new int[]{1, 2, 3, 4, 5, 6, 7})))
                .hasSize(5040).doesNotHaveDuplicates();
    }

    @Test
    void returnsIndependentLists() {
        List<List<Integer>> result = Solution.permutations(new int[]{1, 2, 3});
        result.get(0).set(0, 99);
        long withNinetyNine = result.stream().filter(list -> list.contains(99)).count();
        assertThat(withNinetyNine).as("lists containing 99 after one edit").isEqualTo(1);
    }

    private static List<String> show(List<List<Integer>> lists) {
        List<String> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            result.add(list.toString());
        }
        return result;
    }
}
