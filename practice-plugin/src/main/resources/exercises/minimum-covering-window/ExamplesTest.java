package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void coversRepeatedRequiredCharacters() {
        assertThat(Solution.minimumWindow("HELLOWORLD", "LLO")).isEqualTo("LLO");
    }

    @Test
    void returnsEmptyWhenACharacterIsMissing() {
        assertThat(Solution.minimumWindow("abc", "d")).isEmpty();
    }

    @Test
    void returnsTheWholeSingleCharacterSource() {
        assertThat(Solution.minimumWindow("a", "a")).isEqualTo("a");
    }
}
