package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesAndExpandsTogether() { assertTransform("acdbbca", "ddcdcdd"); }
    @Test
    void removesAllBsBeforeRemainingText() { assertTransform("bbc", "c"); }
    @Test
    void leavesOtherCharactersInOrder() { assertTransform("xy", "xy"); }
    private static void assertTransform(String input, String expected) { char[] buffer = new char[input.length() * 2 + 4]; input.getChars(0, input.length(), buffer, 0); int length = Solution.replaceAndRemove(buffer, input.length()); assertThat(new String(buffer, 0, length)).isEqualTo(expected); }
}
