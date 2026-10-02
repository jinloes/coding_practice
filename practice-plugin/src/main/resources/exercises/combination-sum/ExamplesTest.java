package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsCombinationsWithReuse() {
        assertThat(normalize(Solution.combinations(new int[]{2, 3, 6, 7}, 7)))
                .containsExactly("[2, 2, 3]", "[7]");
    }

    @Test
    void reportsNoCombination() {
        assertThat(Solution.combinations(new int[]{2, 4}, 5)).isEmpty();
    }

    @Test
    void reportsEachMultisetOnce() {
        assertThat(normalize(Solution.combinations(new int[]{1, 2}, 3)))
                .containsExactly("[1, 1, 1]", "[1, 2]");
    }

    private static List<String> normalize(List<List<Integer>> combinations) {
        List<String> result = new ArrayList<>();
        for (List<Integer> combination : combinations) {
            List<Integer> sorted = new ArrayList<>(combination);
            Collections.sort(sorted);
            result.add(sorted.toString());
        }
        Collections.sort(result);
        return result;
    }
}
