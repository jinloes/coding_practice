package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reversesSeveralWords() { assertThat(Solution.reverseWords("time to code")).isEqualTo("code to time"); }
    @Test
    void collapsesExtraSpaces() { assertThat(Solution.reverseWords("  hello   world  ")).isEqualTo("world hello"); }
    @Test
    void singleWordStaysTheSame() { assertThat(Solution.reverseWords("solo")).isEqualTo("solo"); }
}
