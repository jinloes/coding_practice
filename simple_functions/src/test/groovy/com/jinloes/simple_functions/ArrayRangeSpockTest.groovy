package com.jinloes.simple_functions

import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

/**
 * Spock unit tests for {@link ArrayRange}
 */
class ArrayRangeSpockTest extends Specification {
    def "Calculating ranges of an empty array"() {
        expect:
        assertThat(ArrayRange.calculateRanges([] as int[])).isEmpty()
    }

    def "Calculating ranges of a null array"() {
        expect:
        assertThat(ArrayRange.calculateRanges(null)).isEmpty()
    }

    def "Calculating ranges on a sorted array"() {
        expect:
        assertThat(ArrayRange.calculateRanges([0, 1, 2, 7, 21, 22, 1098, 1099, 2000] as int[]))
                .containsExactly("0-2", "7", "21-22", "1098-1099", "2000")
    }
}
