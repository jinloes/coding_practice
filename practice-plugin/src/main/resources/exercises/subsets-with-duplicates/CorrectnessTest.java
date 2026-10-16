package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyInput() {
        assertThat(show(Solution.subsets(new int[]{})))
                .containsExactlyInAnyOrder("[]");
    }

    @Test
    void duplicatesNotAdjacentInInput() {
        assertThat(show(Solution.subsets(new int[]{2, 1, 2})))
                .containsExactlyInAnyOrder("[1, 2, 2]", "[1, 2]", "[1]", "[2, 2]", "[2]", "[]");
    }

    @Test
    void twoPairs() {
        assertThat(show(Solution.subsets(new int[]{1, 1, 2, 2})))
                .containsExactlyInAnyOrder("[1, 1, 2, 2]", "[1, 1, 2]", "[1, 1]", "[1, 2, 2]", "[1, 2]", "[1]", "[2, 2]", "[2]", "[]");
    }

    @Test
    void allDistinctBehavesLikeThePowerSet() {
        assertThat(show(Solution.subsets(new int[]{3, 1, 2})))
                .containsExactlyInAnyOrder("[1, 2, 3]", "[1, 2]", "[1, 3]", "[1]", "[2, 3]", "[2]", "[3]", "[]");
    }

    @Test
    void mixedRepeats() {
        assertThat(show(Solution.subsets(new int[]{5, 5, 5, 6, 6, 7})))
                .containsExactlyInAnyOrder("[5, 5, 5, 6, 6, 7]", "[5, 5, 5, 6, 6]", "[5, 5, 5, 6, 7]", "[5, 5, 5, 6]", "[5, 5, 5, 7]", "[5, 5, 5]", "[5, 5, 6, 6, 7]", "[5, 5, 6, 6]", "[5, 5, 6, 7]", "[5, 5, 6]", "[5, 5, 7]", "[5, 5]", "[5, 6, 6, 7]", "[5, 6, 6]", "[5, 6, 7]", "[5, 6]", "[5, 7]", "[5]", "[6, 6, 7]", "[6, 6]", "[6, 7]", "[6]", "[7]", "[]");
    }

    @Test
    void negativeRepeats() {
        assertThat(show(Solution.subsets(new int[]{-2, -2, 0})))
                .containsExactlyInAnyOrder("[-2, -2, 0]", "[-2, -2]", "[-2, 0]", "[-2]", "[0]", "[]");
    }

    @Test
    void doesNotMutateTheInput() {
        int[] values = {2, 1, 2};
        Solution.subsets(values);
        assertThat(values).containsExactly(2, 1, 2);
    }

    @Test
    void countsDistinctSubsetsByMultiplicity() {
        assertThat(show(Solution.subsets(new int[]{1, 1, 2, 2, 2, 3}))).hasSize(24)
                .doesNotHaveDuplicates();
    }

    @Test
    void handlesLongRuns() {
        int[] values = new int[40];
        java.util.Arrays.fill(values, 8);
        assertThat(Solution.subsets(values)).hasSize(41);
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
