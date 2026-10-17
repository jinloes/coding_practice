package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void expandsOneWildcard() { assertThat(Solution.expand("1?0")).containsExactly("100", "110"); }
    @Test
    void expandsTwoWildcardsInOrder() { assertThat(Solution.expand("??")).containsExactly("00", "01", "10", "11"); }
    @Test
    void patternWithoutWildcardsReturnsItself() { assertThat(Solution.expand("101")).containsExactly("101"); }
}
