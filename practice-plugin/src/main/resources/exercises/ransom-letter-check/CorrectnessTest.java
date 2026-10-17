package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyLetterIsAlwaysConstructible() { assertThat(Solution.canConstruct("", "")).isTrue(); assertThat(Solution.canConstruct("", "anything")).isTrue(); }
    @Test
    void spacesAndPunctuationAreCharacters() { assertThat(Solution.canConstruct("a a!", "!aa ")).isTrue(); assertThat(Solution.canConstruct("a a!", "!aaa")).isFalse(); }
    @Test
    void extraMagazineCharactersDoNotMatter() { assertThat(Solution.canConstruct("cab", "abracadabra")).isTrue(); }
    @Test
    void caseDifferencesDoNotSubstitute() { assertThat(Solution.canConstruct("AA", "Aa")).isFalse(); }
    @Test
    void handlesLargeRepeatedInput() { String magazine = "abc".repeat(5000); assertThat(Solution.canConstruct("cba".repeat(4000), magazine)).isTrue(); assertThat(Solution.canConstruct("cba".repeat(5000) + "d", magazine)).isFalse(); }
}
