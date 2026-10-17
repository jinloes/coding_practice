package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversBoundariesAndDuplicates() { assertThat(Solution.firstGreaterThan(new int[0], 1)).isEqualTo(-1); assertThat(Solution.firstGreaterThan(new int[]{2,2,2}, 2)).isEqualTo(-1); assertThat(Solution.firstGreaterThan(new int[]{-5,-1,0}, -2)).isEqualTo(1); assertThat(Solution.firstGreaterThan(new int[]{4,4,5,5,6}, 4)).isEqualTo(2); }
    @Test
    void doesNotMutate() { int[] a={1,2,2,4}; int[] b=a.clone(); Solution.firstGreaterThan(a,2); assertThat(a).containsExactly(b); }
}

