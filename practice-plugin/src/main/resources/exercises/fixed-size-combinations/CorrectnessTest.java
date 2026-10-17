package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesSingleElementCombinations() { assertThat(Solution.combinations(3, 1)).containsExactly(List.of(1), List.of(2), List.of(3)); }
    @Test
    void handlesFullSetAndNegativeK() { assertThat(Solution.combinations(4, 4)).containsExactly(List.of(1, 2, 3, 4)); assertThat(Solution.combinations(4, -1)).isEmpty(); }
}
