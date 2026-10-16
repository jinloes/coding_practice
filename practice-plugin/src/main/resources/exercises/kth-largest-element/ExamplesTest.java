package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheSecondLargest() {
        assertThat(Solution.findKthLargest(new int[]{9, 4, 7, 1, 8}, 2)).as("findKthLargest of [9, 4, 7, 1, 8] with k=2").isEqualTo(8);
    }

    @Test
    void repeatedValuesCountSeparately() {
        assertThat(Solution.findKthLargest(new int[]{5, 5, 5, 2}, 3)).as("findKthLargest of [5, 5, 5, 2] with k=3").isEqualTo(5);
    }

    @Test
    void singleValueIsLargest() {
        assertThat(Solution.findKthLargest(new int[]{-3}, 1)).as("findKthLargest of [-3] with k=1").isEqualTo(-3);
    }

}
