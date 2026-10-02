package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNoCandidates() {
        assertThat(Solution.combinations(new int[0], 4)).isEmpty();
    }

    @Test
    void usesASingleCandidateRepeatedly() {
        assertThat(normalize(Solution.combinations(new int[]{3}, 9))).containsExactly("[3, 3, 3]");
        assertThat(Solution.combinations(new int[]{3}, 10)).isEmpty();
    }

    @Test
    void usesACandidateEqualToTheTargetOnce() {
        assertThat(normalize(Solution.combinations(new int[]{5}, 5))).containsExactly("[5]");
    }

    @Test
    void ignoresCandidatesLargerThanTheTarget() {
        assertThat(normalize(Solution.combinations(new int[]{10, 20, 4}, 8))).containsExactly("[4, 4]");
    }

    @Test
    void doesNotReportPermutationsAsSeparateCombinations() {
        assertThat(normalize(Solution.combinations(new int[]{2, 3, 5}, 8)))
                .containsExactly("[2, 2, 2, 2]", "[2, 3, 3]", "[3, 5]");
    }

    @Test
    void acceptsCandidatesInAnyOrder() {
        assertThat(normalize(Solution.combinations(new int[]{7, 2, 6, 3}, 7)))
                .containsExactly("[2, 2, 3]", "[7]");
    }

    @Test
    void countsAllWaysUsingOnesAndTwos() {
        List<List<Integer>> result = Solution.combinations(new int[]{1, 2}, 10);
        assertThat(result).hasSize(6);
        assertThat(result).allSatisfy(combination ->
                assertThat(combination.stream().mapToInt(Integer::intValue).sum()).isEqualTo(10));
        assertThat(normalize(result)).doesNotHaveDuplicates();
    }

    @Test
    void handlesALargerTarget() {
        List<List<Integer>> result = Solution.combinations(new int[]{4, 6, 9}, 36);
        assertThat(result).allSatisfy(combination ->
                assertThat(combination.stream().mapToInt(Integer::intValue).sum()).isEqualTo(36));
        assertThat(normalize(result)).doesNotHaveDuplicates().hasSize(7);
    }

    @Test
    void doesNotMutateTheCandidates() {
        int[] candidates = {6, 2, 4};
        Solution.combinations(candidates, 8);
        assertThat(candidates).containsExactly(6, 2, 4);
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
