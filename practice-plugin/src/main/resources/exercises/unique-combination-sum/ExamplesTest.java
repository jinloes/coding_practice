package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void usesEachPositionOnceAndSkipsRepeats() {
        assertThat(show(Solution.combinations(new int[]{4, 1, 1, 2, 3}, 5)))
                .containsExactlyInAnyOrder("[1, 1, 3]", "[1, 4]", "[2, 3]");
    }

    @Test
    void limitsACopyCountToItsOccurrences() {
        assertThat(show(Solution.combinations(new int[]{2, 2, 2}, 4)))
                .containsExactlyInAnyOrder("[2, 2]");
    }

    @Test
    void reportsNoCombination() {
        assertThat(Solution.combinations(new int[]{3, 5}, 4)).isEmpty();
    }

    private static List<String> show(List<List<Integer>> combinations) {
        List<String> result = new ArrayList<>();
        for (List<Integer> combination : combinations) {
            List<Integer> sorted = new ArrayList<>(combination);
            Collections.sort(sorted);
            result.add(sorted.toString());
        }
        return result;
    }
}
