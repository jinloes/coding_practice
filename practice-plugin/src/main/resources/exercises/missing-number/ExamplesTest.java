package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAGapInsideTheRange() {
        assertThat(Solution.missingNumber(new int[]{3, 0, 1})).as("missingNumber([3, 0, 1])").isEqualTo(2);
    }

    @Test
    void findsTheLargestValueWhenItIsMissing() {
        assertThat(Solution.missingNumber(new int[]{0, 1})).as("missingNumber([0, 1])").isEqualTo(2);
    }

    @Test
    void findsTheGapInAnUnorderedArray() {
        assertThat(Solution.missingNumber(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}))
                .as("missingNumber([9, 6, 4, 2, 3, 5, 7, 0, 1])")
                .isEqualTo(8);
    }
}
