package com.jinloes.simple_functions

import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spock unit tests for {@link ArrayOccurrence}
 */
class ArrayOccurrenceTest extends Specification {
  def "empty array test"() {
    given: "an empty array"
    def arr = [] as int[]
    when: "find the number of occurrences of a number in an array"
    def result = ArrayOccurrence.findOccurrences(arr, 5)
    then: "expect a correct result"
    assertThat(result).isEqualTo(0)
  }

  def "Null array test"() {
    given: "a null array"
    def arr = null as int[]
    when: "find the number of occurrences of a number in an array"
    def result = ArrayOccurrence.findOccurrences(arr, 5)
    then: "expect a correct result"
    assertThat(result).isEqualTo(0)
  }

  def "#num should occur #times in #arr"() {
    expect: "a correct result"
    assertThat(ArrayOccurrence.findOccurrences(arr as int[], num)).isEqualTo(times)
    where:
    arr                                                      || num || times
    [5]                                                      || 5   || 1
    [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 6   || 3
    [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 1   || 2
    [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 13  || 2
    [1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13] || 4   || 4
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]            || 1   || 15
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]            || 2   || 0
  }
}
