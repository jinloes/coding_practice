package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void listsAllSixOrderings() {
        assertThat(show(Solution.permutations(new int[]{1, 2, 3})))
                .containsExactlyInAnyOrder("[1, 2, 3]", "[1, 3, 2]", "[2, 1, 3]", "[2, 3, 1]", "[3, 1, 2]", "[3, 2, 1]");
    }

    @Test
    void emptyInputHasOneEmptyOrdering() {
        assertThat(show(Solution.permutations(new int[]{})))
                .containsExactlyInAnyOrder("[]");
    }

    @Test
    void swapsTwoValues() {
        assertThat(show(Solution.permutations(new int[]{4, 9})))
                .containsExactlyInAnyOrder("[4, 9]", "[9, 4]");
    }

    private static List<String> show(List<List<Integer>> lists) {
        List<String> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            result.add(list.toString());
        }
        return result;
    }
}
