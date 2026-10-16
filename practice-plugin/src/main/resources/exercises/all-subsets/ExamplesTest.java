package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void listsEverySubsetOfThreeValues() {
        assertThat(show(Solution.subsets(new int[]{1, 2, 3})))
                .containsExactlyInAnyOrder("[]", "[1]", "[2]", "[3]", "[1, 2]", "[1, 3]", "[2, 3]", "[1, 2, 3]");
    }

    @Test
    void emptyInputHasOnlyTheEmptySubset() {
        assertThat(show(Solution.subsets(new int[]{})))
                .containsExactlyInAnyOrder("[]");
    }

    @Test
    void handlesNegativeValues() {
        assertThat(show(Solution.subsets(new int[]{5, -1})))
                .containsExactlyInAnyOrder("[]", "[-1]", "[5]", "[-1, 5]");
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
