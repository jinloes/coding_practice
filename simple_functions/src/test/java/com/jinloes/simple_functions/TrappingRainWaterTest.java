package com.jinloes.simple_functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TrappingRainWaterTest {
    private TrappingRainWater trappingRainWater;

    @BeforeEach
    void setUp() {
        trappingRainWater = new TrappingRainWater();
    }

    @ParameterizedTest
    @MethodSource("trapArgs")
    void trap(int[] arr, int result) {
        assertThat(trappingRainWater.trap(arr))
                .isEqualTo(result);
    }

    private static Stream<Arguments> trapArgs() {
        return Stream.of(
                Arguments.of(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, 6),
                Arguments.of(new int[]{4, 2, 3}, 1)
        );
    }
}