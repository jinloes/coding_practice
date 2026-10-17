package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyPattern() { assertThat(Solution.expand("")).containsExactly(""); }
    @Test
    void preservesFixedBitsAroundManyWildcards() { assertThat(Solution.expand("?1?")).containsExactly("010", "011", "110", "111"); }
}
