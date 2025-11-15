package com.jinloes.arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RescueBoatsTest {
    private RescueBoats rescueBoats;

    @BeforeEach
    void setUp() {
        rescueBoats = new RescueBoats();
    }

    @ParameterizedTest
    @MethodSource("numRescueBoatsArgs")
    void numRescueBoats(int[] boats, int limit, int expected) {
        assertThat(rescueBoats.numRescueBoats(boats, limit))
                .isEqualTo(expected);
    }

    private static Stream<Arguments> numRescueBoatsArgs() {
        return Stream.of(
                Arguments.of(new int[]{1, 4, 2}, 6, 2),
                Arguments.of(new int[]{3, 5, 3, 4}, 5, 4),
                Arguments.of(new int[]{1, 3, 2, 3, 2}, 3, 4),
                Arguments.of(new int[]{2, 2, 2, 2}, 4, 2)
        );
    }
}