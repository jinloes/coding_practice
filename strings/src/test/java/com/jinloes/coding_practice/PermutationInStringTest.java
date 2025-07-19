package com.jinloes.coding_practice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PermutationInStringTest {
  private PermutationInString permutationInString;

  @BeforeEach
  void setUp() {
    permutationInString = new PermutationInString();
  }

  @ParameterizedTest
  @MethodSource("checkInclusionArgs")
  void checkInclusion(String s1, String s2, boolean expected) {
    assertThat(permutationInString.checkInclusion(s1, s2)).isEqualTo(expected);
  }

  private static Stream<Arguments> checkInclusionArgs() {
    return Stream.of(
        Arguments.of("ab", "lecabee", true),
        Arguments.of("abc", "lecaabee", false),
        Arguments.of("ab", "eidboaoo", false)
    );
  }
}