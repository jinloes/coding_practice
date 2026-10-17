package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInputs() {
        assertThat(Solution.removeSelected("", "abc")).isEmpty();
        assertThat(Solution.removeSelected("abc", "")).isEqualTo("abc");
    }

    @Test
    void isCaseSensitive() {
        assertThat(Solution.removeSelected("AaAa", "a")).isEqualTo("AA");
    }

    @Test
    void duplicateRemovalCharactersBehaveLikeASet() {
        assertThat(Solution.removeSelected("banana", "aaaann")).isEqualTo("b");
    }

    @Test
    void matchesReferenceFilterOnSeededText() {
        Random random = new Random(7);
        String alphabet = "abcdef";
        for (int trial = 0; trial < 100; trial++) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < 80; i++) {
                text.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            String remove = "ace";
            String expected = text.toString().chars()
                    .filter(ch -> remove.indexOf(ch) < 0)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            assertThat(Solution.removeSelected(text.toString(), remove)).isEqualTo(expected);
        }
    }
}
