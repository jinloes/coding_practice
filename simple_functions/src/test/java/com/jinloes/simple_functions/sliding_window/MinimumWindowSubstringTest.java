package com.jinloes.simple_functions.sliding_window;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MinimumWindowSubstringTest {
    private MinimumWindowSubstring minimumWindowSubstring;

    @BeforeEach
    public void setUp() throws Exception {
        minimumWindowSubstring = new MinimumWindowSubstring();
    }

    @ParameterizedTest
    @MethodSource("stringArgs")
    public void minWindow(String s1, String s2, String expected) {
        assertThat(minimumWindowSubstring.minWindow(s1, s2)).isEqualTo(expected);
    }

    private static Stream<Arguments> stringArgs() {
        return Stream.of(
                Arguments.of("ADOBECODEBANC", "ABC", "BANC"),
                Arguments.of("a", "a", "a"),
                Arguments.of("bba", "ab", "ba"),
                Arguments.of("aaaaaaaaaaaabbbbbcdd", "abcdd", "abbbbbcdd"),
                Arguments.of("a", "aa", ""),
                Arguments.of("a", "b", "")
        );
    }


}