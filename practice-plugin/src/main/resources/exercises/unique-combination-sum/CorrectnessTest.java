package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNoCandidates() {
        assertThat(Solution.combinations(new int[]{}, 4)).isEmpty();
    }

    @Test
    void singleCandidateEqualToTarget() {
        assertThat(show(Solution.combinations(new int[]{5}, 5)))
                .containsExactlyInAnyOrder("[5]");
    }

    @Test
    void doesNotReuseASingleCandidate() {
        assertThat(Solution.combinations(new int[]{3}, 6)).isEmpty();
    }

    @Test
    void reportsEachMultisetOnce() {
        assertThat(show(Solution.combinations(new int[]{1, 1, 1, 1}, 2)))
                .containsExactlyInAnyOrder("[1, 1]");
    }

    @Test
    void ignoresCandidatesLargerThanTarget() {
        assertThat(show(Solution.combinations(new int[]{9, 1, 2, 8, 6, 1, 5}, 8)))
                .containsExactlyInAnyOrder("[8]", "[1, 1, 6]", "[1, 2, 5]", "[2, 6]");
    }

    @Test
    void acceptsCandidatesInAnyOrder() {
        assertThat(show(Solution.combinations(new int[]{5, 2, 3, 2}, 7)))
                .containsExactlyInAnyOrder("[2, 2, 3]", "[2, 5]");
    }

    @Test
    void usesAllCandidatesWhenTheyExactlyMatch() {
        assertThat(show(Solution.combinations(new int[]{1, 2, 3}, 6)))
                .containsExactlyInAnyOrder("[1, 2, 3]");
    }

    @Test
    void mixesRepeatedAndSingleValues() {
        assertThat(show(Solution.combinations(new int[]{2, 5, 2, 1, 2}, 5)))
                .containsExactlyInAnyOrder("[1, 2, 2]", "[5]");
    }

    @Test
    void doesNotMutateCandidates() {
        int[] candidates = {3, 1, 2, 1};
        Solution.combinations(candidates, 4);
        assertThat(candidates).containsExactly(3, 1, 2, 1);
    }

    @Test
    void handlesManyEqualCandidates() {
        int[] candidates = new int[16];
        java.util.Arrays.fill(candidates, 1);
        assertThat(show(Solution.combinations(candidates, 8))).hasSize(1);
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
