package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversEdgesAndDuplicates() { assertThat(Solution.insertPosition(new int[0], 3)).isZero(); assertThat(Solution.insertPosition(new int[]{1,1,1}, 1)).isZero(); assertThat(Solution.insertPosition(new int[]{2,4,6}, 1)).isZero(); assertThat(Solution.insertPosition(new int[]{2,4,6}, 5)).isEqualTo(2); }
    @Test
    void doesNotMutate() { int[] a={1,3,5,6}; int[] b=a.clone(); Solution.insertPosition(a,2); assertThat(a).containsExactly(b); }
}

