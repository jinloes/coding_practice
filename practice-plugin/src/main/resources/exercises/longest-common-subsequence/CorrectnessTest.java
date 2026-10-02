package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyStrings() {
        assertThat(Solution.lcsLength("", "")).isEqualTo(0);
        assertThat(Solution.lcsLength("abc", "")).isEqualTo(0);
        assertThat(Solution.lcsLength("", "abc")).isEqualTo(0);
    }

    @Test
    void handlesIdenticalStrings() {
        assertThat(Solution.lcsLength("sequence", "sequence")).isEqualTo(8);
    }

    @Test
    void isDifferentFromTheLongestCommonSubstring() {
        assertThat(Solution.lcsLength("abcdef", "abxcdyef")).isEqualTo(6);
        assertThat(Solution.lcsLength("abxcd", "abycd")).isEqualTo(4);
    }

    @Test
    void isSymmetricInItsArguments() {
        assertThat(Solution.lcsLength("AGGTAB", "GXTXAYB")).isEqualTo(4);
        assertThat(Solution.lcsLength("GXTXAYB", "AGGTAB")).isEqualTo(4);
    }

    @Test
    void handlesRepeatedCharacters() {
        assertThat(Solution.lcsLength("aaaa", "aa")).isEqualTo(2);
        assertThat(Solution.lcsLength("aabb", "bbaa")).isEqualTo(2);
    }

    @Test
    void handlesReversedStrings() {
        assertThat(Solution.lcsLength("abcd", "dcba")).isEqualTo(1);
    }

    @Test
    void handlesLongerInputs() {
        assertThat(Solution.lcsLength("ab".repeat(500), "ba".repeat(500))).isEqualTo(999);
    }

    @Test
    void handlesNonLetterCharacters() {
        assertThat(Solution.lcsLength("a-1 b_2", "-1_2 ")).isEqualTo(4);
    }
}
