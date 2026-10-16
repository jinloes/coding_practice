package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergesSubsetsFromEqualValues() {
        assertThat(show(Solution.subsets(new int[]{1, 2, 2})))
                .containsExactlyInAnyOrder("[1, 2, 2]", "[1, 2]", "[1]", "[2, 2]", "[2]", "[]");
    }

    @Test
    void countsRepeatedCopies() {
        assertThat(show(Solution.subsets(new int[]{4, 4, 4})))
                .containsExactlyInAnyOrder("[4, 4, 4]", "[4, 4]", "[4]", "[]");
    }

    @Test
    void singleValue() {
        assertThat(show(Solution.subsets(new int[]{0})))
                .containsExactlyInAnyOrder("[0]", "[]");
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
