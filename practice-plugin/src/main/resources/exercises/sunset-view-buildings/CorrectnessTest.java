package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndMonotonicInputs() {
        assertThat(Solution.sunsetViews(List.of())).isEmpty();
        assertThat(Solution.sunsetViews(List.of(1, 2, 3, 4))).containsExactly(3);
        assertThat(Solution.sunsetViews(List.of(4, 3, 2, 1))).containsExactly(0, 1, 2, 3);
    }

    @Test
    void randomizedAgainstBruteForce() {
        Random random = new Random(13);
        List<Integer> heights = new ArrayList<>();
        for (int i = 0; i < 250; i++) heights.add(random.nextInt(50));
        assertThat(Solution.sunsetViews(heights)).isEqualTo(bruteForce(heights));
    }

    private static List<Integer> bruteForce(List<Integer> heights) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < heights.size(); i++) {
            boolean visible = true;
            for (int j = i + 1; j < heights.size(); j++) visible &= heights.get(j) < heights.get(i);
            if (visible) result.add(i);
        }
        return result;
    }
}
