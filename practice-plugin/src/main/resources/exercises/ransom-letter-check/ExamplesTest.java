package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void magazineContainsAllNeededCharacters() { assertThat(Solution.canConstruct("note", "tone and more")).isTrue(); }
    @Test
    void missingDuplicateCharacterFails() { assertThat(Solution.canConstruct("aa", "ab")).isFalse(); }
    @Test
    void matchingIsCaseSensitive() { assertThat(Solution.canConstruct("A", "aA")).isTrue(); }
}
