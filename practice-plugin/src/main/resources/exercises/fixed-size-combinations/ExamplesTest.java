package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void listsPairsInLexicographicOrder() { assertThat(Solution.combinations(4, 2)).containsExactly(List.of(1, 2), List.of(1, 3), List.of(1, 4), List.of(2, 3), List.of(2, 4), List.of(3, 4)); }
    @Test
    void zeroSizeCombinationIsTheEmptyChoice() { assertThat(Solution.combinations(3, 0)).containsExactly(List.of()); }
    @Test
    void impossibleSizeReturnsEmptyList() { assertThat(Solution.combinations(2, 3)).isEmpty(); }
}
