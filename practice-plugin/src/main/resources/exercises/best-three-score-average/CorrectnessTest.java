package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void usesOnlyTheBestThreeScoresAndHandlesNegatives() {
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Kai", 100), new Solution.Score("Kai", 1),
                new Solution.Score("Kai", 100), new Solution.Score("Kai", 100), new Solution.Score("Mia", 90),
                new Solution.Score("Mia", 90), new Solution.Score("Mia", 90)))).isEqualTo("Kai");
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Low", -10), new Solution.Score("Low", -20),
                new Solution.Score("Low", -30), new Solution.Score("High", -1), new Solution.Score("High", -2),
                new Solution.Score("High", -3)))).isEqualTo("High");
    }

    @Test
    void handlesLargeInputAndDoesNotMutateList() {
        List<Solution.Score> records = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            records.add(new Solution.Score("S" + i, i));
            records.add(new Solution.Score("S" + i, i + 1));
            records.add(new Solution.Score("S" + i, i + 2));
        }
        List<Solution.Score> before = List.copyOf(records);
        assertThat(Solution.bestThreeAverage(records)).isEqualTo("S99");
        assertThat(records).containsExactlyElementsOf(before);
    }
}
