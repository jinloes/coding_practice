package com.jinloes.arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LongestSubarrayWithDiffTest {
    private final LongestSubarrayWithDiff longestSubarray = new LongestSubarrayWithDiff();

    @ParameterizedTest
    @MethodSource("cases")
    void longestSubarray(int[] nums, int limit, int expected) {
        assertThat(longestSubarray.longestSubarray(nums, limit)).isEqualTo(expected);
    }

    static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of(new int[]{8, 2, 4, 7}, 4, 2),
            Arguments.of(new int[]{10, 1, 2, 4, 7, 2}, 5, 4),
            Arguments.of(new int[]{4, 2, 2, 2, 4, 4, 2, 2}, 2, 8),
            Arguments.of(new int[]{1, 1, 1, 1}, 0, 4),       // all equal, limit 0
            Arguments.of(new int[]{1, 5}, 10, 2),              // entire array within limit
            Arguments.of(new int[]{1, 100}, 0, 1),             // limit 0, no pair qualifies
            Arguments.of(new int[]{1}, 0, 1),                  // single element
            Arguments.of(new int[]{2, 4, 6, 8}, 2, 2)         // stride of 2, window size 2
        );
    }
}
