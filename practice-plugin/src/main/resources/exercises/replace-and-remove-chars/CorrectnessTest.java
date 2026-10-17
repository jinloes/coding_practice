package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyPrefixStaysEmpty() { char[] buffer = new char[4]; assertThat(Solution.replaceAndRemove(buffer, 0)).isZero(); }
    @Test
    void allAsDoubleToDs() { assertTransform("aaa", "dddddd"); }
    @Test
    void allBsDisappear() { assertTransform("bbbb", ""); }
    @Test
    void interleavedCharactersKeepOrder() { assertTransform("cabana", "cddddndd"); }
    @Test
    void largerGeneratedBufferMatchesReference() { String input = "abcz".repeat(2000); assertTransform(input, reference(input)); }
    private static void assertTransform(String input, String expected) { char[] buffer = new char[input.length() * 2 + 4]; input.getChars(0, input.length(), buffer, 0); int length = Solution.replaceAndRemove(buffer, input.length()); assertThat(new String(buffer, 0, length)).isEqualTo(expected); }
    private static String reference(String input) { StringBuilder out = new StringBuilder(); for (int i = 0; i < input.length(); i++) { char c = input.charAt(i); if (c == 'a') out.append("dd"); else if (c != 'b') out.append(c); } return out.toString(); }
}
